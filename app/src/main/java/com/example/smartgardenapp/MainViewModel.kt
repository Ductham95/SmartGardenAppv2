package com.example.smartgardenapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GardenState())
    val uiState = _uiState.asStateFlow()

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val notificationHelper = NotificationHelper(application)
    
    // Track last alert time để tránh spam notifications
    private var lastAlertTimes = mutableMapOf<AlertType, Long>()
    private val ALERT_COOLDOWN_MS = 5 * 60 * 1000L // 5 phút

    // ID thiết bị và Token (được lưu sau khi login)
    // Lưu ý: Device ID lấy từ ThingsBoard, không phải Access Token trong code ESP32
    private val DEVICE_ID = "4450be20-d89e-11f0-a9c3-a94cc0e19399"
    private var authToken: String? = null

    // --- Xử lý Login ---
    fun login(username: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(username, pass))
                if (response.isSuccessful && response.body() != null) {
                    authToken = response.body()!!.token
                    saveToken(authToken!!) // Lưu vào SharedPreferences
                    connectWebSocket(authToken!!)

                    // Chuyển về Main thread để báo kết quả
                    launch(Dispatchers.Main) { onResult(true) }
                } else {
                    launch(Dispatchers.Main) { onResult(false) }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    // --- WebSocket ---
    private fun connectWebSocket(token: String) {
        val request = Request.Builder()
            .url("wss://eu.thingsboard.cloud/api/ws/plugins/telemetry?token=$token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _uiState.value = _uiState.value.copy(isConnected = true)

                // Gửi lệnh Subscribe telemetry
                val subscribeMsg = """
                {
                    "tsSubCmds": [
                        {
                            "entityType": "DEVICE",
                            "entityId": "$DEVICE_ID", 
                            "scope": "LATEST_TELEMETRY",
                            "cmdId": 1
                        }
                    ],
                    "historyCmds": [],
                    "attrSubCmds": []
                }
                """.trimIndent()
                webSocket.send(subscribeMsg)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                parseTelemetry(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _uiState.value = _uiState.value.copy(isConnected = false)
            }
        })
    }

    private fun parseTelemetry(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            if (!json.has("data")) return

            val data = json.getJSONObject("data")
            // Helper để lấy giá trị từ cấu trúc: "key": [[ts, "value"]]
            fun getValue(key: String): Float? {
                return if (data.has(key)) {
                    data.getJSONArray(key).getJSONArray(0).getString(1).toFloatOrNull()
                } else null
            }

            // Cập nhật State mới (khớp với key trong main.cpp của bạn)
            _uiState.value = _uiState.value.copy(
                temperature = getValue("temperature") ?: _uiState.value.temperature,
                humidity = getValue("humidity") ?: _uiState.value.humidity,
                soilMoisture = getValue("soilMoisture") ?: _uiState.value.soilMoisture,
                tankWaterLevel = getValue("tankWaterLevel") ?: _uiState.value.tankWaterLevel,
                batteryLevel = getValue("batteryLevel") ?: _uiState.value.batteryLevel,
                isPumpOn = (getValue("pumpState") ?: 0f) == 1f // pumpState gửi lên là 1 hoặc 0 (true/false)
            )
            
            // Kiểm tra và gửi cảnh báo
            checkAndSendAlerts()
        } catch (e: Exception) {
            Log.e("ViewModel", "Parse Error: ${e.message}")
        }
    }
    
    // Kiểm tra ngưỡng và gửi cảnh báo
    private fun checkAndSendAlerts() {
        val state = _uiState.value
        val settings = state.alertSettings
        val activeAlerts = mutableListOf<AlertType>()
        val currentTime = System.currentTimeMillis()
        
        // Kiểm tra độ ẩm đất
        if (settings.soilMoistureEnabled && state.soilMoisture < settings.soilMoistureThreshold) {
            activeAlerts.add(AlertType.SOIL_MOISTURE_LOW)
            if (shouldSendNotification(AlertType.SOIL_MOISTURE_LOW, currentTime)) {
                notificationHelper.sendSoilMoistureAlert(state.soilMoisture, settings.soilMoistureThreshold)
                lastAlertTimes[AlertType.SOIL_MOISTURE_LOW] = currentTime
            }
        }
        
        // Kiểm tra mực nước
        if (settings.waterLevelEnabled && state.tankWaterLevel < settings.waterLevelThreshold) {
            activeAlerts.add(AlertType.WATER_LEVEL_LOW)
            if (shouldSendNotification(AlertType.WATER_LEVEL_LOW, currentTime)) {
                notificationHelper.sendWaterLevelAlert(state.tankWaterLevel, settings.waterLevelThreshold)
                lastAlertTimes[AlertType.WATER_LEVEL_LOW] = currentTime
            }
        }
        
        // Kiểm tra nhiệt độ
        if (settings.temperatureEnabled) {
            if (state.temperature > settings.temperatureMaxThreshold) {
                activeAlerts.add(AlertType.TEMPERATURE_HIGH)
                if (shouldSendNotification(AlertType.TEMPERATURE_HIGH, currentTime)) {
                    notificationHelper.sendTemperatureAlert(
                        state.temperature,
                        settings.temperatureMinThreshold,
                        settings.temperatureMaxThreshold,
                        isHigh = true
                    )
                    lastAlertTimes[AlertType.TEMPERATURE_HIGH] = currentTime
                }
            } else if (state.temperature < settings.temperatureMinThreshold) {
                activeAlerts.add(AlertType.TEMPERATURE_LOW)
                if (shouldSendNotification(AlertType.TEMPERATURE_LOW, currentTime)) {
                    notificationHelper.sendTemperatureAlert(
                        state.temperature,
                        settings.temperatureMinThreshold,
                        settings.temperatureMaxThreshold,
                        isHigh = false
                    )
                    lastAlertTimes[AlertType.TEMPERATURE_LOW] = currentTime
                }
            }
        }
        
        // Kiểm tra pin
        if (settings.batteryEnabled && state.batteryLevel > 0 && state.batteryLevel < settings.batteryThreshold) {
            activeAlerts.add(AlertType.BATTERY_LOW)
            if (shouldSendNotification(AlertType.BATTERY_LOW, currentTime)) {
                notificationHelper.sendBatteryAlert(state.batteryLevel, settings.batteryThreshold)
                lastAlertTimes[AlertType.BATTERY_LOW] = currentTime
            }
        }
        
        // Cập nhật active alerts
        _uiState.value = _uiState.value.copy(activeAlerts = activeAlerts)
    }
    
    private fun shouldSendNotification(alertType: AlertType, currentTime: Long): Boolean {
        val lastTime = lastAlertTimes[alertType] ?: 0L
        return (currentTime - lastTime) > ALERT_COOLDOWN_MS
    }

    // --- Điều khiển Bơm (RPC) ---
    fun togglePump(isOn: Boolean) {
        if (authToken == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val rpc = RpcRequest(method = "setPump", params = isOn)
            RetrofitClient.instance.sendRpcRequest("Bearer $authToken", DEVICE_ID, rpc)
            // Cập nhật UI ngay lập tức cho mượt (thực tế sẽ được cập nhật lại qua WebSocket)
            _uiState.value = _uiState.value.copy(isPumpOn = isOn)
        }
    }

    // Lấy lịch sử dữ liệu cảm biến
    fun fetchSensorHistory() {
        if (authToken == null) {
            Log.e("ViewModel", "Auth token is null!")
            // Thêm dữ liệu giả để test UI
            loadMockData()
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val endTs = System.currentTimeMillis()
                val startTs = endTs - (7 * 24 * 60 * 60 * 1000) // 7 days ago (tăng lên để có nhiều dữ liệu hơn)
                
                Log.d("ViewModel", "Fetching history from $startTs to $endTs for device $DEVICE_ID")
                
                val response = RetrofitClient.instance.getTelemetryHistory(
                    token = "Bearer $authToken",
                    deviceId = DEVICE_ID,
                    keys = "temperature,humidity,temp,hum,Temperature,Humidity", // Thử nhiều key names
                    startTs = startTs,
                    endTs = endTs,
                    limit = 100
                )
                
                Log.d("ViewModel", "Response code: ${response.code()}")
                
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("ViewModel", "Response body: $data")
                    Log.d("ViewModel", "Available keys in response: ${data.keySet()}")
                    
                    // Parse temperature history - thử nhiều key names
                    val tempHistory = mutableListOf<SensorDataPoint>()
                    val tempKeys = listOf("temperature", "temp", "Temperature", "TEMPERATURE")
                    for (key in tempKeys) {
                        if (data.has(key)) {
                            Log.d("ViewModel", "Found temperature data with key: $key")
                            val tempArray = data.getAsJsonArray(key)
                            Log.d("ViewModel", "Temperature entries: ${tempArray.size()}")
                            for (i in 0 until tempArray.size()) {
                                try {
                                    val item = tempArray[i].asJsonObject
                                    val ts = item.get("ts").asLong
                                    val value = item.get("value").asString.toDoubleOrNull() ?: continue
                                    tempHistory.add(SensorDataPoint(timestamp = ts, value = value))
                                } catch (e: Exception) {
                                    Log.e("ViewModel", "Error parsing temp item: ${e.message}")
                                }
                            }
                            break
                        }
                    }
                    
                    if (tempHistory.isEmpty()) {
                        Log.w("ViewModel", "No temperature data found with any known key")
                    }
                    
                    // Parse humidity history - thử nhiều key names
                    val humidityHistory = mutableListOf<SensorDataPoint>()
                    val humKeys = listOf("humidity", "hum", "Humidity", "HUMIDITY")
                    for (key in humKeys) {
                        if (data.has(key)) {
                            Log.d("ViewModel", "Found humidity data with key: $key")
                            val humArray = data.getAsJsonArray(key)
                            Log.d("ViewModel", "Humidity entries: ${humArray.size()}")
                            for (i in 0 until humArray.size()) {
                                try {
                                    val item = humArray[i].asJsonObject
                                    val ts = item.get("ts").asLong
                                    val value = item.get("value").asString.toDoubleOrNull() ?: continue
                                    humidityHistory.add(SensorDataPoint(timestamp = ts, value = value))
                                } catch (ex: Exception) {
                                    Log.e("ViewModel", "Error parsing humidity item: ${ex.message}")
                                }
                            }
                            break
                        }
                    }
                    
                    if (humidityHistory.isEmpty()) {
                        Log.w("ViewModel", "No humidity data found with any known key")
                    }
                    
                    Log.d("ViewModel", "Parsed ${tempHistory.size} temp points, ${humidityHistory.size} humidity points")
                    
                    // Nếu không có dữ liệu thật, dùng dữ liệu giả
                    if (tempHistory.isEmpty() && humidityHistory.isEmpty()) {
                        Log.w("ViewModel", "No data from API, using mock data. Device may not have historical data yet. Make sure ESP32 is sending data to ThingsBoard.")
                        loadMockData()
                    } else {
                        // Update state
                        _uiState.value = _uiState.value.copy(
                            temperatureHistory = tempHistory.sortedBy { it.timestamp },
                            humidityHistory = humidityHistory.sortedBy { it.timestamp }
                        )
                    }
                } else {
                    Log.e("ViewModel", "API Error: ${response.code()} - ${response.errorBody()?.string()}")
                    // Dùng dữ liệu giả khi lỗi API
                    loadMockData()
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching history: ${e.message}", e)
                // Dùng dữ liệu giả khi có exception
                loadMockData()
            }
        }
    }
    
    // Dữ liệu giả để test UI
    private fun loadMockData() {
        val now = System.currentTimeMillis()
        val tempHistory = mutableListOf<SensorDataPoint>()
        val humidityHistory = mutableListOf<SensorDataPoint>()
        
        // Tạo 24 điểm dữ liệu (1 điểm mỗi giờ)
        for (i in 0..23) {
            val timestamp = now - ((23 - i) * 60 * 60 * 1000)
            
            // Nhiệt độ dao động từ 25-35 độ C
            val temp = 25.0 + (kotlin.math.sin(i * 0.5) * 5.0) + (Math.random() * 2.0)
            tempHistory.add(SensorDataPoint(timestamp, temp))
            
            // Độ ẩm dao động từ 60-80%
            val humidity = 70.0 + (kotlin.math.cos(i * 0.3) * 10.0) + (Math.random() * 3.0)
            humidityHistory.add(SensorDataPoint(timestamp, humidity))
        }
        
        _uiState.value = _uiState.value.copy(
            temperatureHistory = tempHistory,
            humidityHistory = humidityHistory
        )
        
        Log.d("ViewModel", "Loaded mock data: ${tempHistory.size} temp points, ${humidityHistory.size} humidity points")
    }

    // Lưu token đơn giản
    private fun saveToken(token: String) {
        val sharedPref = getApplication<Application>().getSharedPreferences("SmartGardenPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("AUTH_TOKEN", token)
            apply()
        }
    }
}