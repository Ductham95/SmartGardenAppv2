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
        } catch (e: Exception) {
            Log.e("ViewModel", "Parse Error: ${e.message}")
        }
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

    // Lưu token đơn giản
    private fun saveToken(token: String) {
        val sharedPref = getApplication<Application>().getSharedPreferences("SmartGardenPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("AUTH_TOKEN", token)
            apply()
        }
    }
}