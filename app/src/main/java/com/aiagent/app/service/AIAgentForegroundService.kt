package com.aiagent.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aiagent.app.MainActivity
import com.aiagent.app.R
import com.aiagent.app.util.GeminiAIClient
import com.aiagent.app.util.TextToSpeechManager
import com.aiagent.app.util.VoiceCommandListener
import com.aiagent.app.receiver.CallReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AIAgentForegroundService : Service() {
    companion object {
        const val CHANNEL_ID = "AIAgentChannel"
        const val NOTIFICATION_ID = 1
        private const val TAG = "AIAgentService"
    }
    
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var voiceCommandListener: VoiceCommandListener
    private lateinit var ttsManager: TextToSpeechManager
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var isListening = false
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        // Initialize TTS
        ttsManager = TextToSpeechManager(this)
        
        // Initialize Speech Recognizer
        initializeSpeechRecognizer()
        
        // Initialize Call Receiver
        CallReceiver.ttsManager = ttsManager
        CallReceiver.voiceCommandListener = if (::voiceCommandListener.isInitialized) voiceCommandListener else null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        
        // Create notification and start as foreground service
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Start listening to voice commands
        startListening()
        
        // Announce that the service is active
        ttsManager.speak("AI Agent is now active. Ready to help you.", delay = 500)
        
        return START_STICKY
    }
    
    private fun createNotification(): Notification {
        createNotificationChannel()
        
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_message))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
    
    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        voiceCommandListener = VoiceCommandListener(this, ::handleVoiceCommand)
        speechRecognizer.setRecognitionListener(voiceCommandListener)
    }
    
    private fun startListening() {
        if (!isListening) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Hindi
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            
            speechRecognizer.startListening(intent)
            isListening = true
            Log.d(TAG, "Voice listening started")
        }
    }
    
    private fun handleVoiceCommand(command: String) {
        Log.d(TAG, "Voice command received: $command")
        val lowerCommand = command.lowercase().trim()
        
        when {
            lowerCommand.contains("utha", ignoreCase = true) -> {
                ttsManager.speak("Answering the call", delay = 100)
                CallReceiver.answerCall(this)
            }
            lowerCommand.contains("cut kar", ignoreCase = true) || lowerCommand.contains("cut", ignoreCase = true) -> {
                ttsManager.speak("Cutting the call", delay = 100)
                CallReceiver.rejectCall(this)
            }
            lowerCommand.contains("whatsapp", ignoreCase = true) || lowerCommand.contains("message", ignoreCase = true) -> {
                handleWhatsAppCommand(command)
            }
            else -> {
                // Send to Gemini AI for response
                serviceScope.launch {
                    val response = GeminiAIClient.getResponse(command)
                    ttsManager.speak(response, delay = 200)
                }
            }
        }
        
        // Restart listening for next command
        startListening()
    }
    
    private fun handleWhatsAppCommand(command: String) {
        // Extract the message and recipient from the command
        // Format: "WhatsApp kar [message] to [name]"
        try {
            val regex = """whatsapp\s+kar\s+(.+?)\s+to\s+(.+)""".toRegex(RegexOption.IGNORE_CASE)
            val matchResult = regex.find(command)
            
            if (matchResult != null) {
                val message = matchResult.groupValues[1]
                val recipient = matchResult.groupValues[2]
                
                ttsManager.speak("Sending message to $recipient", delay = 100)
                Log.d(TAG, "WhatsApp Message: '$message' to '$recipient'")
                // TODO: Integrate with WhatsApp API or use intent to open WhatsApp with pre-filled message
            } else {
                ttsManager.speak("Could not extract message and recipient", delay = 100)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing WhatsApp command: ${e.message}")
            ttsManager.speak("Error processing WhatsApp command", delay = 100)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        isListening = false
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        if (::ttsManager.isInitialized) {
            ttsManager.shutdown()
        }
        serviceScope.coroutineContext.cancel()
    }
}
