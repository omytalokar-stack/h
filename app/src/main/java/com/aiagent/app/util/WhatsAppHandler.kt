package com.aiagent.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class WhatsAppHandler {
    companion object {
        private const val TAG = "WhatsAppHandler"
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        
        fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
            try {
                val cleanPhoneNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                
                // Check if WhatsApp is installed
                val isWhatsAppInstalled = isPackageInstalled(context, WHATSAPP_PACKAGE)
                val isWhatsAppBusinessInstalled = isPackageInstalled(context, WHATSAPP_BUSINESS_PACKAGE)
                
                if (!isWhatsAppInstalled && !isWhatsAppBusinessInstalled) {
                    Log.e(TAG, "WhatsApp is not installed")
                    return
                }
                
                // Create WhatsApp message intent
                val url = "https://wa.me/$cleanPhoneNumber?text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    setPackage(if (isWhatsAppInstalled) WHATSAPP_PACKAGE else WHATSAPP_BUSINESS_PACKAGE)
                }
                
                context.startActivity(intent)
                Log.d(TAG, "WhatsApp intent launched for $cleanPhoneNumber")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error sending WhatsApp message: ${e.message}")
            }
        }
        
        fun openWhatsAppChat(context: Context, phoneNumber: String) {
            try {
                val cleanPhoneNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                
                val isWhatsAppInstalled = isPackageInstalled(context, WHATSAPP_PACKAGE)
                val isWhatsAppBusinessInstalled = isPackageInstalled(context, WHATSAPP_BUSINESS_PACKAGE)
                
                if (!isWhatsAppInstalled && !isWhatsAppBusinessInstalled) {
                    Log.e(TAG, "WhatsApp is not installed")
                    return
                }
                
                val url = "https://wa.me/$cleanPhoneNumber"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    setPackage(if (isWhatsAppInstalled) WHATSAPP_PACKAGE else WHATSAPP_BUSINESS_PACKAGE)
                }
                
                context.startActivity(intent)
                Log.d(TAG, "Opened WhatsApp chat with $cleanPhoneNumber")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error opening WhatsApp chat: ${e.message}")
            }
        }
        
        private fun isPackageInstalled(context: Context, packageName: String): Boolean {
            return try {
                context.packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
        
        fun isWhatsAppAvailable(context: Context): Boolean {
            return isPackageInstalled(context, WHATSAPP_PACKAGE) || 
                   isPackageInstalled(context, WHATSAPP_BUSINESS_PACKAGE)
        }
    }
}
