package com.aiagent.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.aiagent.app.util.TextToSpeechManager

class SMSReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SMSReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "SMS received")
        
        try {
            val bundle = intent.extras ?: return
            val pdus = bundle.get("pdus") as? Array<*> ?: return
            
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val senderNumber = smsMessage.originatingAddress
                val messageBody = smsMessage.messageBody
                
                Log.d(TAG, "Message from: $senderNumber - Content: $messageBody")
                
                // Announce the SMS via TTS
                val ttsManager = TextToSpeechManager(context)
                val announcement = "New message from $senderNumber. Message: $messageBody"
                ttsManager.speak(announcement, delay = 500)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing SMS: ${e.message}", e)
        }
    }
}
