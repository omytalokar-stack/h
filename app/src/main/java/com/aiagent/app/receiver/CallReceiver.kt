package com.aiagent.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.aiagent.app.util.TextToSpeechManager
import com.aiagent.app.util.VoiceCommandListener
import com.aiagent.app.util.ContactsManager

class CallReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "CallReceiver"
        var ttsManager: TextToSpeechManager? = null
        var voiceCommandListener: VoiceCommandListener? = null
        
        fun answerCall(context: Context) {
            try {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // Implementation depends on the phone's API level and manufacturer
                Log.d(TAG, "Attempting to answer call")
            } catch (e: Exception) {
                Log.e(TAG, "Error answering call: ${e.message}")
            }
        }
        
        fun rejectCall(context: Context) {
            try {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // Implementation depends on the phone's API level and manufacturer
                Log.d(TAG, "Attempting to reject call")
            } catch (e: Exception) {
                Log.e(TAG, "Error rejecting call: ${e.message}")
            }
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Broadcast received: ${intent.action}")
        
        when (intent.action) {
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                handlePhoneStateChange(context, intent)
            }
            Intent.ACTION_NEW_OUTGOING_CALL -> {
                handleOutgoingCall(context, intent)
            }
        }
    }
    
    private fun handlePhoneStateChange(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Unknown"
        
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.d(TAG, "Phone ringing: $incomingNumber")
                announceCallerName(context, incomingNumber)
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d(TAG, "Call answered")
                ttsManager?.speak("Call started", delay = 100)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d(TAG, "Call ended")
                ttsManager?.speak("Call ended", delay = 100)
            }
        }
    }
    
    private fun handleOutgoingCall(context: Context, intent: Intent) {
        val outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) ?: "Unknown"
        Log.d(TAG, "Outgoing call to: $outgoingNumber")
        
        val contactName = ContactsManager.getContactName(context, outgoingNumber)
        val message = "Calling $contactName"
        ttsManager?.speak(message, delay = 100)
    }
    
    private fun announceCallerName(context: Context, phoneNumber: String) {
        try {
            val contactName = ContactsManager.getContactName(context, phoneNumber)
            val announcement = if (contactName != null) {
                "Incoming call from $contactName"
            } else {
                "Incoming call from $phoneNumber"
            }
            
            Log.d(TAG, "Announcing: $announcement")
            ttsManager?.speak(announcement, delay = 500)
        } catch (e: Exception) {
            Log.e(TAG, "Error announcing caller: ${e.message}")
            ttsManager?.speak("Incoming call from $phoneNumber", delay = 500)
        }
    }
}
