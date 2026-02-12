# Quick Start Guide - AI Agent Android

**⚡ Get started in 5 minutes!**

## 1️⃣ Get Gemini API Key (1 min)

1. Go to: https://aistudio.google.com/apikey
2. Click "Create API Key in new project"
3. Copy your API key

## 2️⃣ Add API Key to Project (1 min)

Edit: `android/app/src/main/java/com/aiagent/app/util/GeminiAIClient.kt`

Line 15:
```kotlin
private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
```

Change to your actual key:
```kotlin
private const val GEMINI_API_KEY = "AIzaSyD_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

## 3️⃣ Build APK (2 min)

### Option A: Android Studio (Easiest)
1. Open: `c:\Users\user\Downloads\game`
2. Click "Build" → "Build APK(s)"
3. Wait for build to complete

### Option B: Command Line
```bash
cd android
./gradlew assembleDebug    # Windows: use gradlew.bat
```

APK Location: `android/app/build/outputs/apk/debug/app-debug.apk`

## 4️⃣ Install & Run (1 min)

### Physical Android Device
```bash
# Check device connected
adb devices

# Install
adb install -r android/app/build/outputs/apk/debug/app-debug.apk

# Run
adb shell am start -n com.aiagent.app/.MainActivity
```

### Android Emulator
- Android Studio → Device Manager → Create/Select device → Play
- Run app via Android Studio's Run button (▶)

## 5️⃣ Grant Permissions

When app launches:
- Tap "Start AI Agent"
- Grant all requested permissions
- Status shows: "AI Agent: ACTIVE ✓"

## 🎤 Test Voice Commands

Once running, speak these commands:

| Command | Result |
|---------|--------|
| **"Utha"** | Answers incoming call |
| **"Cut kar"** | Rejects call |
| **"WhatsApp kar hello to John"** | Opens WhatsApp |
| **"Who is the US president?"** | Gets Gemini AI response |

## 📋 Project Structure

```
game/
├── .github/workflows/main.yml    ← CI/CD pipeline
├── android/                       ← Android app
│   ├── app/src/main/
│   │   ├── java/com/aiagent/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── service/
│   │   │   ├── receiver/
│   │   │   └── util/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   ├── gradlew & gradlew.bat
│   └── ...
├── package.json                  ← Node metadata
├── README.md                      ← Full documentation
├── SETUP_GUIDE.md                 ← Detailed setup
├── FEATURES.md                    ← Feature docs
└── PROJECT_STRUCTURE.md           ← Complete structure
```

## 🛠️ Useful Commands

```bash
# Clean & rebuild
cd android && ./gradlew clean assembleDebug

# View logs
adb logcat -s "AIAgent"

# Uninstall
adb uninstall com.aiagent.app

# Check device status
adb devices
```

## ⚠️ Common Issues

### Build Fails
```
Solution: 
cd android
./gradlew clean
./gradlew assembleDebug
```

### Permissions Not Working
```
Solution:
adb uninstall com.aiagent.app
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

### Voice Not Working
```
Solution:
- Check device microphone
- Check app has RECORD_AUDIO permission
- Check device volume is on
```

### Gemini API Error
```
Solution:
- Verify API key is correct in GeminiAIClient.kt
- Check API key at: https://aistudio.google.com/apikey
- Verify internet connection
```

## 📚 Learn More

- **Full Setup**: [SETUP_GUIDE.md](SETUP_GUIDE.md)
- **All Features**: [FEATURES.md](FEATURES.md)
- **Project Details**: [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)
- **Main Docs**: [README.md](README.md)

## 🚀 Next Steps

1. ✅ Follow this quick start
2. ✅ Test app on device
3. ✅ Read [FEATURES.md](FEATURES.md) for advanced usage
4. ✅ Customize voice commands
5. ✅ Build release APK for distribution

## 💡 Tips

- Disable battery optimization for this app so it doesn't get killed
- Use good internet for Gemini AI responses
- Train the voice recognizer with your accent
- Test on multiple devices for best results

---

**Ready to build? Start now! 🎉**

Questions? Check the detailed guides above!
