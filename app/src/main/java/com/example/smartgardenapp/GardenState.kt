package com.example.smartgardenapp

// State chứa tất cả dữ liệu cần hiển thị
data class GardenState(
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val soilMoisture: Float = 0f,
    val tankWaterLevel: Float = 0f,
    val batteryLevel: Float = 0f,
    val isPumpOn: Boolean = false,
    val isConnected: Boolean = false,
    val temperatureHistory: List<SensorDataPoint> = emptyList(),
    val humidityHistory: List<SensorDataPoint> = emptyList(),
    val alertSettings: AlertSettings = AlertSettings(),
    val activeAlerts: List<AlertType> = emptyList(),
    val wateringSchedules: List<WateringSchedule> = emptyList()
)

// Data class cho lịch sử dữ liệu cảm biến
data class SensorDataPoint(
    val timestamp: Long,  // Unix timestamp in milliseconds
    val value: Double
)

// Cài đặt ngưỡng cảnh báo
data class AlertSettings(
    val soilMoistureEnabled: Boolean = true,
    val soilMoistureThreshold: Float = 30f,
    
    val waterLevelEnabled: Boolean = true,
    val waterLevelThreshold: Float = 20f,
    
    val temperatureEnabled: Boolean = true,
    val temperatureMinThreshold: Float = 15f,
    val temperatureMaxThreshold: Float = 40f,
    
    val batteryEnabled: Boolean = true,
    val batteryThreshold: Float = 20f
)

// Các loại cảnh báo
enum class AlertType {
    SOIL_MOISTURE_LOW,
    WATER_LEVEL_LOW,
    TEMPERATURE_HIGH,
    TEMPERATURE_LOW,
    BATTERY_LOW
}