package com.aiagent.app.util

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class GeminiAIClient {
    companion object {
        private const val TAG = "GeminiAIClient"
        
        // Replace with your actual Gemini API key
        // Get it from: https://aistudio.google.com/apikey
        private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
        
        private var model: GenerativeModel? = null
        
        private fun initializeModel() {
            if (model == null) {
                try {
                    model = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = GEMINI_API_KEY
                    )
                    Log.d(TAG, "Gemini model initialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing Gemini model: ${e.message}")
                }
            }
        }
        
        suspend fun getResponse(prompt: String): String {
            return withContext(Dispatchers.IO) {
                try {
                    if (GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
                        Log.w(TAG, "Gemini API key not configured. Using default response.")
                        return@withContext "Please configure your Gemini API key in GeminiAIClient.kt to use AI features."
                    }
                    
                    initializeModel()
                    
                    val response = model?.generateContent(prompt)
                    val responseText = response?.text ?: "Unable to generate response"
                    
                    Log.d(TAG, "Gemini Response: $responseText")
                    return@withContext responseText
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting Gemini response: ${e.message}")
                    return@withContext "Sorry, I encountered an error processing your request."
                }
            }
        }
        
        suspend fun getResponseInLanguage(prompt: String, language: String): String {
            return withContext(Dispatchers.IO) {
                try {
                    val enhancedPrompt = "Please respond in $language. User query: $prompt"
                    return@withContext getResponse(enhancedPrompt)
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting multilingual response: ${e.message}")
                    return@withContext "Sorry, I encountered an error processing your request."
                }
            }
        }
    }
}
