package com.aiagent.app.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    companion object {
        private const val TAG = "TextToSpeechManager"
    }
    
    private var tts: TextToSpeech? = null
    private val handler = Handler(Looper.getMainLooper())
    
    init {
        tts = TextToSpeech(context, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "TextToSpeech initialized successfully")
            
            // Set language to English and Hindi support
            val result = tts?.setLanguage(Locale("en", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Language not supported, using default (English)")
                tts?.setLanguage(Locale.US)
            }
            
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.9f)
        } else {
            Log.e(TAG, "TextToSpeech Initialization Failed")
        }
    }
    
    fun speak(text: String, delay: Long = 0) {
        if (delay > 0) {
            handler.postDelayed({
                speakImmediate(text)
            }, delay)
        } else {
            speakImmediate(text)
        }
    }
    
    private fun speakImmediate(text: String) {
        try {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            Log.d(TAG, "Speaking: $text")
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking: ${e.message}")
        }
    }
    
    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            Log.d(TAG, "TextToSpeech shut down")
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS: ${e.message}")
        }
    }
}
