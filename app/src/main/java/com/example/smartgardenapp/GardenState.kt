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
    val humidityHistory: List<SensorDataPoint> = emptyList()
)

// Data class cho lịch sử dữ liệu cảm biến
data class SensorDataPoint(
    val timestamp: Long,  // Unix timestamp in milliseconds
    val value: Double
)