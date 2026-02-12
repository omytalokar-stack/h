# Architecture Documentation - AI Agent Android

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      USER INTERFACE LAYER                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  MainActivity.kt - Control Panel UI                      │  │
│  │  - Start/Stop Service buttons                            │  │
│  │  - Status display                                        │  │
│  │  - Permission requests                                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER (Always-On)                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  AIAgentForegroundService.kt - Core Service             │  │
│  │  ├─ Persistent notification (never killed)              │  │
│  │  ├─ Voice recognition (SpeechRecognizer)                │  │
│  │  ├─ Command processing                                  │  │
│  │  └─ Service state management                            │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
           ↓                          ↓                      ↓
    ┌─────────────┐         ┌─────────────────┐      ┌──────────┐
    │   VOICE     │         │   BROADCAST     │      │   API    │
    │ PROCESSING  │         │   RECEIVERS     │      │  CLIENTS │
    └─────────────┘         └─────────────────┘      └──────────┘
           ↓                          ↓                      ↓
    ┌─────────────────────────────────────────────────────────┐
    │         UTILITY LAYER & INTEGRATIONS                   │
    │  ┌──────────────────────────────────────────────────┐  │
    │  │ • TextToSpeechManager → Android TTS Engine      │  │
    │  │ • VoiceCommandListener → Speech Recognition     │  │
    │  │ • GeminiAIClient → Google AI API                │  │
    │  │ • ContactsManager → Device Contacts DB         │  │
    │  │ • WhatsAppHandler → WhatsApp Integration       │  │
    │  │ • PermissionManager → System Permissions       │  │
    │  │ • CallReceiver → Incoming Calls                │  │
    │  │ • SMSReceiver → Text Messages                  │  │
    │  └──────────────────────────────────────────────────┘  │
    └─────────────────────────────────────────────────────────┘
           ↓                      ↓                    ↓
    ┌─────────────┐      ┌──────────────┐      ┌──────────────┐
    │   ANDROID   │      │   EXTERNAL   │      │   GEMINI     │
    │   SYSTEM    │      │   SERVICES   │      │      API     │
    │   (TTS,     │      │(WhatsApp,SM) │      │  (AI Brain)  │
    │  Speech)    │      │              │      │              │
    └─────────────┘      └──────────────┘      └──────────────┘
```

## Component Interaction Flow

### 1. App Launch Flow
```
User taps app icon
         ↓
MainActivity.onCreate()
         ↓
RequestPermissions()
         ↓
User grants permissions
         ↓
startAIAgent() triggered
         ↓
startForegroundService(AIAgentForegroundService)
         ↓
Service onCreate() → Initialize speech recognizer
         ↓
Service onStartCommand() → Start foreground notification
         ↓
startListening() → Begin voice command recognition
         ↓
Announcement: "AI Agent is now active"
```

### 2. Voice Command Processing Flow
```
Speech detected by microphone
         ↓
SpeechRecognizer processes audio
         ↓
VoiceCommandListener.onResults() called
         ↓
handleVoiceCommand(recognized text)
         ↓
Match against command patterns:
  ├─ "utha" → Answer call
  ├─ "cut kar" → Reject call
  ├─ "whatsapp kar" → WhatsApp integration
  └─ Other → Send to Gemini AI
         ↓
Execute action
         ↓
Speak response (TTS)
         ↓
startListening() → Resume listening
```

### 3. Incoming Call Flow
```
Phone rings (call detected)
         ↓
CallReceiver.onReceive() triggered
         ↓
TelephonyManager broadcasts PHONE_STATE_CHANGED
         ↓
handlePhoneStateChange()
         ↓
Lookup caller name in contacts:
  ├─ Contact found → Get name
  └─ Contact not found → Use phone number
         ↓
Announce via TTS: "Call from [Name/Number]"
         ↓
Listen for voice commands:
  ├─ "Utha" → Answer call
  └─ "Cut kar" → Reject call
         ↓
Send call state to UI (MainActivity)
```

### 4. AI Response Flow
```
User voice input
         ↓
Not matching call/WhatsApp commands
         ↓
GeminiAIClient.getResponse(prompt)
         ↓
Coroutine launched (async, non-blocking)
         ↓
API call to Gemini:
  POST /generateContent
  with: prompt, API key, model name
         ↓
Google Gemini API processes
         ↓
Response received
         ↓
TextToSpeechManager.speak(response)
         ↓
User hears AI answer
```

## Module Responsibilities

### Core Components

#### 1. **MainActivity.kt** - UI Controller
```
Responsibility: User interface and initial setup
├─ Display app status
├─ Handle Start/Stop buttons
├─ Request runtime permissions
├─ Launch foreground service
└─ Monitor service status
```

#### 2. **AIAgentForegroundService.kt** - Service Provider
```
Responsibility: Orchestrate all background operations
├─ Manage foreground notification
├─ Initialize speech recognizer
├─ Listen for voice commands
├─ Parse and route commands
├─ Coordinate with other components
├─ Maintain service lifecycle
└─ Handle ERROR/IDLE states
```

#### 3. **Broadcast Receivers** - Event Handlers
```
CallReceiver.kt
├─ Detect incoming calls
├─ Announce caller information
└─ Trigger call handling commands

SMSReceiver.kt
├─ Detect incoming SMS
├─ Announce message content
└─ Optional: route to AI
```

### Utility Modules

#### 4. **GeminiAIClient.kt** - AI Integration
```
Responsibility: Communicate with Google Gemini API
├─ Initialize AI model
├─ Send prompts
├─ Receive responses
├─ Handle API errors
├─ Support multilingual input
└─ Manage API key
```

#### 5. **TextToSpeechManager.kt** - Voice Output
```
Responsibility: Text-to-Speech functionality
├─ Initialize TTS engine
├─ Handle language selection
├─ Manage speech queue
├─ Apply speech parameters (pitch, rate)
├─ Support delayed speech
└─ Handle TTS errors
```

#### 6. **VoiceCommandListener.kt** - Voice Input Processor
```
Responsibility: Process speech recognition results
├─ Monitor speech events
├─ Capture recognized text
├─ Handle recognition errors
├─ Return results to callback
└─ Log speech events
```

#### 7. **ContactsManager.kt** - Contact Database Access
```
Responsibility: Query device contacts
├─ Lookup contact by phone number
├─ Retrieve contact name
├─ Get all contacts list
├─ Handle database queries
└─ Manage ContentProvider access
```

#### 8. **WhatsAppHandler.kt** - Third-party Integration
```
Responsibility: WhatsApp integration
├─ Detect WhatsApp installation
├─ Send messages via URL scheme
├─ Open chat window
├─ Format phone numbers
└─ Handle WhatsApp errors
```

#### 9. **PermissionManager.kt** - System Utilities
```
Responsibility: Permission and service utilities
├─ Check service running status
├─ Create notification channels
├─ Manage permission states
└─ Handle Android version differences
```

## Data Flow Diagrams

### Command Processing Pipeline
```
SpeechRecognizer
     ↓
VoiceCommandListener
     ↓
onResults(Bundle)
     ↓
CommandParser
     ├─ Pattern matching
     └─ Extract parameters
     ↓
Command Router
     ├─→ CallHandler
     ├─→ WhatsAppHandler
     └─→ GeminiAIClient
     ↓
ActionExecutor
     ├─→ TTS Speaker
     └─→ System APIs
```

### Service Lifecycle
```
START
  ↓
onCreate()
  ├─ Initialize TTS
  ├─ Initialize SpeechRecognizer
  └─ Setup listeners
  ↓
onStartCommand()
  ├─ Create notification
  ├─ startForeground()
  └─ Begin listening
  ↓
RUNNING (listener loop)
  ├─ Listen for voice
  ├─ Process commands
  ├─ Execute actions
  └─ Speak responses
  ↓
onDestroy()
  ├─ Stop listening
  ├─ Release TTS
  └─ Cleanup resources
  ↓
STOP
```

## Thread & Concurrency Model

### Main Thread
- UI operations (MainActivity)
- TTS speech (TextToSpeechManager)
- Service lifecycle

### Background Threads
- Voice recognition (SpeechRecognizer internal)
- Broadcast receivers (system threads)
- Gemini API calls (Coroutines with Dispatchers.IO)

### Coroutines
```kotlin
serviceScope.launch {
    // Gemini API calls happen here
    // Non-blocking, survives configuration changes
}
```

## Permission Model

### Declared Permissions (AndroidManifest.xml)
```xml
13 permissions required:
├─ Phone: ANSWER_PHONE_CALLS, READ_PHONE_STATE, CALL_PHONE
├─ Contacts: READ_CONTACTS
├─ Audio: RECORD_AUDIO, MODIFY_AUDIO_SETTINGS
├─ System: SYSTEM_ALERT_WINDOW, INTERNET, FOREGROUND_SERVICE
├─ Messaging: SEND_SMS, READ_SMS
└─ Notifications: POST_NOTIFICATIONS
```

### Runtime Permission Flow
```
App Launch
  ↓
Check each permission
  ├─ Already granted? Continue
  └─ Not granted? Request from user
  ↓
RequestPermissionResult
  ├─ All granted? Start service
  └─ Some denied? Show error
```

## Error Handling Strategy

### Call Chain Error Propagation
```
Try-Catch blocks at:
├─ Service methods
├─ Receiver callbacks  
├─ API calls
├─ Database queries
└─ TTS operations
    ↓
Log error with TAG
    ↓
Speak error message to user
    ↓
Resume safe state
```

### Recovery Mechanisms
```
Voice Recognition Error
  → Retry listening automatically

API Connection Error
  → Return cached response or fallback message

Permission Missing
  → Show warning, request again

TTS Not Available
  → Log to console instead
```

## Performance Considerations

### Memory Management
- Singleton pattern for TTS manager
- Lazy initialization of Gemini model
- Release speech recognizer in onDestroy()
- Avoid memory leaks in coroutines

### CPU Usage
- Background threads for heavy operations
- Async/coroutines for non-blocking calls
- Suspend listening during processing
- ProGuard optimization for release builds

### Battery Impact
- Foreground service with notification
- Efficient speech recognition
- Minimal polling/timer usage
- User can disable battery optimization

### Network Efficiency
- Batch Gemini API calls
- Cache responses if needed
- Use WiFi for slower connections
- Fallback for offline messages

## Integration Points

### External Systems
```
┌─ Google Gemini API (AI Responses)
│
├─ Android ContactsContract (Contact Lookup)
│
├─ Android TelephonyManager (Call Detection)
│
├─ Android SpeechRecognizer (Voice Input)
│
├─ Android TextToSpeech (Voice Output)
│
└─ WhatsApp (Message Sending)
```

## Extension Points

### Easy to Add
- New voice commands (pattern match in AIAgentForegroundService)
- New TTS languages (change Locale)
- New AI prompts (modify GeminiAIClient)

### Moderate Effort
- Different AI backend (replace GeminiAIClient)
- Custom notification (modify createNotification)
- Advanced speech recognition (upgrade engine)

### High Effort
- Offline mode support
- Machine learning local models
- Home automation integration
- Multi-user support

## Security Architecture

### Password/API Key Protection
- Never hardcode in production
- Use environment variables
- GitHub Secrets for CI/CD
- Encrypted storage (SharedPreferences encrypted)

### Permission Boundaries
- Only request required permissions
- Principle of least privilege
- Verify permissions before use
- Secure broadcast receivers

### Data Privacy
- No analytics/tracking
- Local contact database only
- Gemini AI is stateless
- No persistent user data storage

## Deployment & CI/CD

### Build Pipeline (GitHub Actions)
```
Push to main
    ↓
Checkout code
    ↓
Setup JDK 17
    ↓
Build APK (debug + release)
    ↓
Upload artifacts
    ↓
Create release (on tags)
```

### Build Variants
```
Debug
├─ No optimization
├─ Debuggable
└─ Fast compilation

Release
├─ ProGuard enabled
├─ Shrink resources
└─ Optimized APK
```

---

**Architecture Version**: 1.0  
**Last Updated**: February 2026  
**Status**: Production Ready
