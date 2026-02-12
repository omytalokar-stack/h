package com.aiagent.app.util

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aiagent.app.R

class PermissionManager {
    companion object {
        private const val TAG = "PermissionManager"
        private const val CHANNEL_ID = "AIAgentChannel"
        
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = activityManager.getRunningServices(Integer.MAX_VALUE)
            
            for (service in services) {
                if (service.service.className == serviceClass.name) {
                    Log.d(TAG, "Service ${serviceClass.simpleName} is running")
                    return true
                }
            }
            
            Log.d(TAG, "Service ${serviceClass.simpleName} is not running")
            return false
        }
        
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.service_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "AI Agent Service Notification Channel"
                    enableVibration(false)
                    enableLights(false)
                }
                
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            }
        }
    }
}
