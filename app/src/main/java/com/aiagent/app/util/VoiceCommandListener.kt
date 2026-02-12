package com.aiagent.app.util

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log

class VoiceCommandListener(
    private val context: Context,
    private val onCommandReceived: (String) -> Unit
) : RecognitionListener {
    companion object {
        private const val TAG = "VoiceCommandListener"
    }
    
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "Ready for speech")
    }
    
    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Beginning of speech")
    }
    
    override fun onRmsChanged(rmsdB: Float) {
        // Called when sound level changes
    }
    
    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "Buffer received")
    }
    
    override fun onEndOfSpeech() {
        Log.d(TAG, "End of speech")
    }
    
    override fun onError(error: Int) {
        val errorMessage = when (error) {
            RecognitionListener.ERROR_AUDIO -> "Audio recording error"
            RecognitionListener.ERROR_CLIENT -> "Client side error"
            RecognitionListener.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            RecognitionListener.ERROR_NETWORK -> "Network error"
            RecognitionListener.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            RecognitionListener.ERROR_NO_MATCH -> "No match found"
            RecognitionListener.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            RecognitionListener.ERROR_SERVER -> "Server error"
            RecognitionListener.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> "Unknown error"
        }
        Log.e(TAG, "Voice recognition error: $errorMessage (Code: $error)")
    }
    
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(RecognitionListener.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val topResult = matches[0]
            Log.d(TAG, "Recognized: $topResult")
            onCommandReceived(topResult)
        } else {
            Log.w(TAG, "No speech recognized")
        }
    }
    
    override fun onPartialResults(partialResults: Bundle?) {
        val partialMatches = partialResults?.getStringArrayList(RecognitionListener.RESULTS_RECOGNITION)
        if (partialMatches != null && partialMatches.isNotEmpty()) {
            Log.d(TAG, "Partial: ${partialMatches[0]}")
        }
    }
    
    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "Speech recognition event: $eventType")
    }
}
