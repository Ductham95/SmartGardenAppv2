package com.example.smartgardenapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ALERTS_ID = "smart_garden_alerts"
        private const val CHANNEL_ALERTS_NAME = "Cảnh báo vườn"
        
        // Notification IDs
        const val NOTIFICATION_SOIL_MOISTURE = 1
        const val NOTIFICATION_WATER_LEVEL = 2
        const val NOTIFICATION_TEMPERATURE = 3
        const val NOTIFICATION_BATTERY = 4
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ALERTS_ID,
                CHANNEL_ALERTS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo cảnh báo về tình trạng vườn"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    fun sendSoilMoistureAlert(currentValue: Float, threshold: Float) {
        if (!hasNotificationPermission()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Độ ẩm đất thấp")
            .setContentText("Độ ẩm đất hiện tại: ${String.format("%.0f", currentValue)}% (ngưỡng: ${String.format("%.0f", threshold)}%)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Độ ẩm đất đang ở mức ${String.format("%.0f", currentValue)}%, thấp hơn ngưỡng cảnh báo ${String.format("%.0f", threshold)}%. Cây trồng có thể cần được tưới nước."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_SOIL_MOISTURE, notification)
        } catch (e: SecurityException) {
            // Permission denied
        }
    }
    
    fun sendWaterLevelAlert(currentValue: Float, threshold: Float) {
        if (!hasNotificationPermission()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("💧 Mực nước thấp")
            .setContentText("Mực nước hiện tại: ${String.format("%.0f", currentValue)}% (ngưỡng: ${String.format("%.0f", threshold)}%)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Mực nước trong bình chứa đang ở mức ${String.format("%.0f", currentValue)}%, thấp hơn ngưỡng ${String.format("%.0f", threshold)}%. Vui lòng đổ thêm nước."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_WATER_LEVEL, notification)
        } catch (e: SecurityException) {
            // Permission denied
        }
    }
    
    fun sendTemperatureAlert(currentValue: Float, minThreshold: Float, maxThreshold: Float, isHigh: Boolean) {
        if (!hasNotificationPermission()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val title = if (isHigh) "🌡️ Nhiệt độ quá cao" else "🌡️ Nhiệt độ quá thấp"
        val threshold = if (isHigh) maxThreshold else minThreshold
        val condition = if (isHigh) "cao hơn" else "thấp hơn"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText("Nhiệt độ: ${String.format("%.1f", currentValue)}°C ($condition ${String.format("%.1f", threshold)}°C)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Nhiệt độ môi trường hiện tại ${String.format("%.1f", currentValue)}°C, $condition ngưỡng cảnh báo ${String.format("%.1f", threshold)}°C. Có thể ảnh hưởng đến cây trồng."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_TEMPERATURE, notification)
        } catch (e: SecurityException) {
            // Permission denied
        }
    }
    
    fun sendBatteryAlert(currentValue: Float, threshold: Float) {
        if (!hasNotificationPermission()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🔋 Pin yếu")
            .setContentText("Pin còn ${String.format("%.0f", currentValue)}% (ngưỡng: ${String.format("%.0f", threshold)}%)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Pin thiết bị đang ở mức ${String.format("%.0f", currentValue)}%, thấp hơn ngưỡng ${String.format("%.0f", threshold)}%. Vui lòng sạc hoặc thay pin."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_BATTERY, notification)
        } catch (e: SecurityException) {
            // Permission denied
        }
    }
}
