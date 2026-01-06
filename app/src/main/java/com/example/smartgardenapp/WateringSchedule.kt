package com.example.smartgardenapp

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data class cho lịch tưới
data class WateringSchedule(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Lịch tưới",
    val hour: Int = 6,  // Giờ (0-23)
    val minute: Int = 0, // Phút (0-59)
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // 1=Thứ 2, 7=Chủ nhật
    val duration: Int = 10000, // Thời gian tưới (ms), mặc định 10 giây
    val isEnabled: Boolean = true
) {
    // Format thời gian hiển thị
    fun getTimeString(): String = String.format("%02d:%02d", hour, minute)
    
    // Format ngày trong tuần
    fun getDaysString(): String {
        if (daysOfWeek.size == 7) return "Hàng ngày"
        val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        return daysOfWeek.sorted().joinToString(", ") { 
            dayNames[it - 1] 
        }
    }
    
    // Convert sang JSON để lưu lên ThingsBoard
    fun toJson(): String = Gson().toJson(this)
    
    companion object {
        // Parse từ JSON
        fun fromJson(json: String): WateringSchedule? {
            return try {
                Gson().fromJson(json, WateringSchedule::class.java)
            } catch (e: Exception) {
                null
            }
        }
        
        // Parse danh sách từ JSON
        fun listFromJson(json: String): List<WateringSchedule> {
            return try {
                val type = object : TypeToken<List<WateringSchedule>>() {}.type
                Gson().fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
        
        // Convert list sang JSON
        fun listToJson(schedules: List<WateringSchedule>): String = Gson().toJson(schedules)
    }
}
