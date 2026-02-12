package com.aiagent.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aiagent.app.service.AIAgentForegroundService
import com.aiagent.app.util.PermissionManager


class MainActivity : AppCompatActivity() {
    private val requiredPermissions = mutableListOf(
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.QUERY_ALL_PACKAGES
    )
    
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var statusTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        statusTextView = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        
        // Add notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PermissionManager.createNotificationChannel(this)
        }
        
        startButton.setOnClickListener {
            requestPermissionsIfNeeded()
        }
        
        stopButton.setOnClickListener {
            stopService(Intent(this, AIAgentForegroundService::class.java))
            statusTextView.text = "AI Agent Stopped"
            Toast.makeText(this, "AI Agent stopped", Toast.LENGTH_SHORT).show()
        }
        
        updateStatus()
    }
    
    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val missingPermissions = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            startAIAgent()
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                startAIAgent()
            } else {
                statusTextView.text = "Missing permissions. Cannot start."
                Toast.makeText(this, "All permissions are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startAIAgent() {
        val intent = Intent(this, AIAgentForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        statusTextView.text = "AI Agent Running..."
        Toast.makeText(this, "AI Agent started successfully", Toast.LENGTH_SHORT).show()
        updateStatus()
    }
    
    private fun updateStatus() {
        val isServiceRunning = PermissionManager.isServiceRunning(this, AIAgentForegroundService::class.java)
        statusTextView.text = if (isServiceRunning) "AI Agent: ACTIVE ✓" else "AI Agent: INACTIVE"
    }
    
    override fun onResume() {
        super.onResume()
        updateStatus()
    }
}
