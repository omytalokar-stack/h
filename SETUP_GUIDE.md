# AI Agent Android - Setup Guide

Complete step-by-step guide to set up and run the AI Agent Android application.

## Prerequisites

### System Requirements
- **Operating System**: Windows 10+, macOS 10.14+, or Linux
- **Java**: JDK 17 or higher
- **RAM**: Minimum 4GB (8GB recommended for Android Studio)
- **Disk Space**: 15GB+ for Android SDK and emulator

### Software Installation

#### 1. Install Java JDK 17
**Windows/Mac/Linux:**
```bash
# Download from: https://www.oracle.com/java/technologies/downloads/#java17
# Or use package manager:
# macOS with Homebrew:
brew install openjdk@17

# Linux (Ubuntu/Debian):
sudo apt-get install openjdk-17-jdk

# Windows (with Chocolatey):
choco install openjdk17
```

Verify installation:
```bash
java -version
# Should show: openjdk version "17.x.x"
```

#### 2. Install Android Studio
- Download from: https://developer.android.com/studio
- Follow the official installation guide for your OS
- During setup, select "Standard" installation (includes SDK, emulator, etc.)

#### 3. Install Android SDK Components
After Android Studio opens:
1. Go to: Settings → Appearance & Behavior → System Settings → Android SDK
2. Ensure these are installed:
   - SDK Platforms: Android 14 (API 34), Android 13 (API 33), Android 8.0 (API 26)
   - SDK Tools:
     - Android SDK Build-Tools 34.0.0
     - Android Emulator
     - Android SDK Platform-Tools
     - Google Play Services (for device testing)

## Project Setup

### 1. Clone or Download Project
```bash
# Navigate to your desired location
cd ~/projects

# If using Git:
git clone <repository-url> ai-agent-android
cd ai-agent-android

# Or download ZIP and extract
unzip ai-agent-android.zip
cd ai-agent-android
```

### 2. Configure Gemini API Key (IMPORTANT)

**Step 1: Get Your Free API Key**
1. Go to: https://aistudio.google.com/apikey
2. Click "Create API Key in new project"
3. Copy your API key

**Step 2: Update the Code**
Open: `android/app/src/main/java/com/aiagent/app/util/GeminiAIClient.kt`

Find this line (around line 13):
```kotlin
private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
```

Replace with your actual key:
```kotlin
private const val GEMINI_API_KEY = "AIzaSyD_xxxxxxxxxxxxxxxxxxxxx"
```

⚠️ **SECURITY WARNING**: Never commit your API key to version control!
- Add this line to `.env.local` or use GitHub Secrets for CI/CD

### 3. Open Project in Android Studio

1. Launch Android Studio
2. Click "Open" → Select the `ai-agent-android` folder
3. Wait for Gradle sync to complete (might take 2-3 minutes)
4. If prompted about SDK updates, click "Install"

### 4. Verify Project Structure

Check that all these folders exist:
```
android/app/src/main/
├── java/com/aiagent/app/
│   ├── MainActivity.kt
│   ├── service/
│   ├── receiver/
│   └── util/
├── res/
│   ├── layout/
│   ├── values/
│   └── drawable/
└── AndroidManifest.xml
```

## Building the Project

### Option 1: Using Android Studio (Recommended)

1. **Connect Android Device or Start Emulator**
   - Physical: Enable USB Debugging (Settings → Developer Options → USB Debugging)
   - Emulator: AVD Manager → Select device → Click Play

2. **Build the App**
   - Click "Build" menu → "Build Bundle(s) / APK(s)" → "Build APK(s)"
   - Wait for build to complete (Gradle output will show "BUILD SUCCESSFUL")

3. **Run the App**
   - Click Run (▶ button) or press Shift+F10
   - Select your device/emulator
   - App will install and launch automatically

### Option 2: Using Command Line

```bash
# Navigate to project root
cd ai-agent-android

# Make gradlew executable (Unix/Mac only)
chmod +x android/gradlew

# Debug build
npm run build:android:debug

# Or directly:
cd android
./gradlew assembleDebug      # Or gradlew.bat on Windows
```

### Option 3: Gradle Wrapper

```bash
cd android

# On Linux/Mac:
./gradlew build

# On Windows:
gradlew.bat build
```

## Installation on Device

### Physical Android Device

```bash
# Find connected devices
adb devices

# Install APK
adb install -r android/app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.aiagent.app/.MainActivity

# View logs
adb logcat -s "AIAgent"
```

### Android Emulator

1. Open Android Studio
2. Tools → Device Manager
3. Create new virtual device with:
   - Phone: Pixel 5 (or any recent model)
   - OS: Android 13 or 14 (API 33+)
   - RAM: 2GB+
4. Click Play to start emulator
5. Run app via Android Studio or ADB

## Initial App Launch

### First Time Setup

1. **Launch the App**
   - Tap "Start AI Agent" button

2. **Grant Permissions**
   - App will request necessary permissions:
     - Manage phone calls
     - Read contacts
     - Record audio
     - System alert window
     - Others as needed
   - Tap "Allow" for all

3. **Test the Service**
   - Status should show: "AI Agent: ACTIVE ✓"
   - A persistent notification should appear

### Testing Voice Commands

#### In the App:
```
"Utha" → Answers calls
"Cut kar" → Rejects/hangs up calls
"WhatsApp kar Message to Contact" → Opens WhatsApp
```

#### Testing with Gemini AI:
```
"Tell me a joke"
"What is the capital of France?"
"What's the weather today?"
```

## Troubleshooting

### Build Issues

**Problem**: Gradle sync failed
```
Solution:
1. File → Invalidate Caches/Restart → Invalidate and Restart
2. Delete: .gradle/ and build/ folders
3. Try syncing again
```

**Problem**: Java version mismatch
```bash
# Verify Java version
java -version

# Should be 17.x.x

# If not, set JAVA_HOME:
# Windows:
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Mac/Linux:
export JAVA_HOME=/usr/libexec/java_home -v 17
```

### Runtime Issues

**Problem**: Permission denied errors in logs
```
Solution:
1. Uninstall app: adb uninstall com.aiagent.app
2. Clear device cache: adb shell pm clear com.aiagent.app
3. Reinstall app
4. Grant all permissions when prompted
```

**Problem**: TTS (voice) not working
```
Solution:
1. Check device volume isn't muted
2. Go to Settings → Accessibility → Text-to-Speech output
3. Install language packs if needed
4. Restart app
```

**Problem**: Microphone not working
```
Solution:
1. Verify app has microphone permission in Settings
2. Test microphone in another app (Voice Recorder)
3. Restart device
4. Check for hardware issues
```

**Problem**: Service keeps stopping
```
Solution:
1. Disable battery optimization:
   Settings → Apps → AI Agent → Battery → Not optimized
2. Disable adaptive battery:
   Settings → Battery → Adaptive Battery → Off
3. Pin app to notification to keep it running
```

### API Issues

**Problem**: Gemini API returns "API Key Error"
```
Solution:
1. Verify key in GeminiAIClient.kt is correct
2. Check API key at: https://aistudio.google.com/apikey
3. If revoked, generate new key
4. Verify device has internet connection
5. Check API quota in Google Cloud Console
```

**Problem**: "Network error" from Gemini
```
Solution:
1. Check internet connection: adb shell ping google.com
2. Verify firewall isn't blocking requests
3. Check if API service is up: https://status.cloud.google.com/
4. Try with different network (mobile vs WiFi)
```

## Development Tips

### View Logs
```bash
# View all logs
adb logcat

# Filter to app only
adb logcat -s "AIAgent"

# Real-time monitoring
adb logcat -f - | grep "AIAgent"

# Save logs to file
adb logcat > app_logs.txt
```

### Debug Mode
In Android Studio:
1. Run → Debug (Shift+F9)
2. Set breakpoints (click line numbers)
3. When hitting breakpoint, inspect variables in Debugger panel

### Useful ADB Commands
```bash
# List all installed apps
adb shell pm list packages

# Uninstall app
adb uninstall com.aiagent.app

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# Start app
adb shell am start -n com.aiagent.app/.MainActivity

# Stop app
adb shell am force-stop com.aiagent.app

# Clear app cache
adb shell pm clear com.aiagent.app
```

## Advanced Configuration

### Change App Package Name
1. Right-click `java/com` folder
2. Refactor → Rename
3. Enter new package name (e.g., `com.mycompany.aiagent`)
4. Update in AndroidManifest.xml and build.gradle.kts

### Modify Voice Language
In `AIAgentForegroundService.kt`, line ~41:
```kotlin
intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Change to your language code
```

Language codes:
- `en-US` = English (US)
- `en-IN` = English (Indian)
- `hi-IN` = Hindi (India)
- `fr-FR` = French
- `es-ES` = Spanish

### Update App Icon
Replace: `android/app/src/main/res/drawable/ic_launcher_foreground.xml`

## Performance Optimization

### ProGuard Settings
Modify: `android/app/proguard-rules.pro`
- Keeps code small and fast
- Makes APK harder to reverse-engineer

### BuildTypes Optimization
```gradle
release {
    minifyEnabled true          // Enable code shrinking
    shrinkResources true       // Remove unused resources
    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
}
```

## CI/CD with GitHub Actions

The project includes `.github/workflows/main.yml` that automatically:
1. Builds APK on each push to `main`
2. Uploads APK artifacts
3. Creates releases on git tags

To test CI/CD locally:
```bash
# Install act: https://github.com/nektos/act
act push

# Or push to GitHub and view Actions tab
git push origin main
# Check: https://github.com/YOUR_USERNAME/ai-agent-android/actions
```

## Next Steps

1. ✅ Complete setup following this guide
2. ✅ Test app on actual device
3. ✅ Customize voice commands in AIAgentForegroundService.kt
4. ✅ Add WhatsApp integration
5. ✅ Build and release APK

## Support & Resources

- **Android Docs**: https://developer.android.com/docs
- **Kotlin Guide**: https://kotlinlang.org/docs/
- **Google Gemini API**: https://ai.google.dev/
- **Stack Overflow**: Tag questions with `android`, `kotlin`

---

**You're all set! Happy coding! 🚀**
