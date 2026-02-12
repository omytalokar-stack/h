# Pure Native Android AI Agent

A cutting-edge Android application that combines AI intelligence with voice control, call interception, and messaging automation.

## Features

✨ **Core Features:**
- **Gemini AI Integration**: Intelligent responses in multiple languages (Hindi, English, etc.)
- **Voice Commands**: 
  - "Utha" = Answer incoming call
  - "Cut kar" = Reject/Hang up
  - "WhatsApp kar [Message] to [Name]" = Send WhatsApp message
- **Call Announcement**: Automatic TTS announcement of caller's name from contacts
- **Always-On Service**: Runs as a foreground service 24/7 without interruption
- **Multilingual Support**: Responds in Hindi, English, and other supported languages

## Project Structure

```
.
├── .github/
│   └── workflows/
│       └── main.yml                    # GitHub Actions CI/CD pipeline
├── android/
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/aiagent/app/
│   │   │   │   │   ├── MainActivity.kt              # Main UI Activity
│   │   │   │   │   ├── service/
│   │   │   │   │   │   └── AIAgentForegroundService.kt  # Always-on service
│   │   │   │   │   ├── receiver/
│   │   │   │   │   │   ├── CallReceiver.kt         # Call interception
│   │   │   │   │   │   └── SMSReceiver.kt          # SMS/Message handling
│   │   │   │   │   └── util/
│   │   │   │   │       ├── GeminiAIClient.kt       # AI integration
│   │   │   │   │       ├── TextToSpeechManager.kt  # TTS functionality
│   │   │   │   │       ├── VoiceCommandListener.kt # Voice recognition
│   │   │   │   │       ├── ContactsManager.kt      # Contact lookup
│   │   │   │   │       ├── WhatsAppHandler.kt      # WhatsApp integration
│   │   │   │   │       └── PermissionManager.kt    # Permission handling
│   │   │   │   ├── res/
│   │   │   │   │   ├── layout/
│   │   │   │   │   │   └── activity_main.xml       # Main UI layout
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── strings.xml
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   └── themes.xml
│   │   │   │   │   └── drawable/
│   │   │   │   │       └── ic_launcher_foreground.xml
│   │   │   │   └── AndroidManifest.xml             # App manifest with permissions
│   │   │   └── test/
│   │   ├── build.gradle.kts                        # App-level Gradle config
│   │   └── proguard-rules.pro                      # Code obfuscation rules
│   ├── build.gradle.kts                            # Project-level Gradle config
│   ├── settings.gradle.kts                         # Gradle settings
│   ├── gradle.properties                           # Gradle properties
├── package.json                                     # Node.js project metadata
├── .gitignore                                       # Git ignore rules
└── README.md                                        # This file

```

## Requirements

- **Android SDK**: API level 26+ (Android 8.0 and above)
- **Java/Kotlin**: JDK 17 or higher
- **Android Studio**: Latest stable version recommended
- **Gemini API Key**: Get it from [Google AI Studio](https://aistudio.google.com/apikey)

## Permissions

The app requires the following Android permissions:

```xml
<!-- Call handling -->
<uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />

<!-- Contacts -->
<uses-permission android:name="android.permission.READ_CONTACTS" />

<!-- Audio -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- System -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.INTERNET" />

<!-- Messaging -->
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.READ_SMS" />

<!-- Service -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Setup Instructions

### 1. Clone Repository
```bash
git clone <repository-url>
cd game
```

### 2. Configure Gemini API Key

Edit `android/app/src/main/java/com/aiagent/app/util/GeminiAIClient.kt`:

```kotlin
private const val GEMINI_API_KEY = "YOUR_ACTUAL_API_KEY_HERE"
```

Get your free API key from: https://aistudio.google.com/apikey

### 3. Build the Project

#### Using Android Studio:
1. Open the project in Android Studio
2. Sync Gradle files
3. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"

#### Using Commands:
```bash
# Debug build
npm run build:android:debug

# Release build
npm run build:android

# Clean build
npm run clean
```

### 4. Install on Device

```bash
# Find your device
adb devices

# Install APK
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/main.yml`) that:

1. ✅ Checks out code
2. ✅ Sets up JDK 17
3. ✅ Installs Android SDK
4. ✅ Builds release and debug APKs
5. ✅ Uploads APK artifacts
6. ✅ Creates releases on git tags

### Trigger Builds:
- Push to `main` branch automatically builds
- Create a git tag to release APK: `git tag v1.0.0 && git push --tags`

## Voice Commands

Once the app is running, use these voice commands:

| Command | Action |
|---------|--------|
| **Utha** | Answer the incoming call |
| **Cut kar** | Reject/Hang up the call |
| **WhatsApp kar [Message] to [Name]** | Send WhatsApp message |
| **Any other command** | Sent to Gemini AI for intelligent response |

## Dependencies

- **AndroidX**
  - appcompat: 1.6.1
  - constraintlayout: 2.1.4
  - lifecycle: 2.6.2

- **Kotlin**
  - stdlib: 1.9.20
  - coroutines: 1.7.3

- **Google AI (Gemini)**
  - generativeai: 0.7.0

- **Networking**
  - OkHttp3: 4.11.0
  - Retrofit2: 2.10.0
  - Gson: 2.10.1

## Troubleshooting

### APK Won't Build
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

### Permissions Not Working
- Ensure you're targeting Android 6.0+ for runtime permissions
- Grant permissions manually in Settings → Apps → AI Agent

### TTS Not Working
- Download language pack in device settings
- Try English (US) as fallback

### Voice Recognition Fails
- Check microphone permissions
- Ensure device is connected to internet
- Verify language is supported

### Gemini API Errors
- Verify API key is correctly set
- Check API quota at [Google Cloud Console](https://console.cloud.google.com)
- Ensure device has internet connection

## Building for Release

1. Generate keystore (one-time):
```bash
keytool -genkey -v -keystore release.keystore \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias my-app-key
```

2. Sign the APK:
```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
    -keystore release.keystore \
    app-release-unsigned.apk my-app-key
```

3. Align the APK:
```bash
zipalign -v 4 app-release-unsigned.apk app-release.apk
```

## Testing on Emulator

1. Create AVD with API 26+ and Google Play Services
2. Record yourself reading commands: Settings → Accessibility → Voice Control
3. Test call simulation: Emulator Console

## License

MIT License - Feel free to use this project as you wish

## Support

For issues or suggestions:
- Create an Issue on GitHub
- Email: support@aiagent.app

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**Built with ❤️ using Kotlin, Android Native, and Google Gemini AI**
