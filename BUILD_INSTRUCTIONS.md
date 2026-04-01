# Build Instructions for ANC Rural Health Android App

This guide will help you build the Android application without Android Studio using command-line tools.

## Prerequisites

### 1. Install Java Development Kit (JDK) 17
- **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [Adoptium](https://adoptium.net/)
- **Linux**: `sudo apt install openjdk-17-jdk`
- **Mac**: `brew install openjdk@17`

Verify installation:
```bash
java -version
```

### 2. Install Android Command Line Tools
1. Download Android Command Line Tools from [Android Developer Website](https://developer.android.com/studio#command-tools)
2. Extract to a directory (e.g., `C:\Android\cmdline-tools` on Windows or `~/Android/cmdline-tools` on Linux/Mac)
3. Create `latest` folder inside `cmdline-tools` and move contents there

### 3. Set Environment Variables

**Windows (PowerShell):**
```powershell
$env:ANDROID_HOME = "C:\Android"
$env:PATH += ";$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools"
```

**Linux/Mac (Bash):**
```bash
export ANDROID_HOME=$HOME/Android
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
```

### 4. Install Android SDK Components
```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
sdkmanager --licenses
```

## Building the Application

### Step 1: Navigate to Project Directory
```bash
cd "c:/Users/CHARBAKROY/Desktop/Mobile App"
```

### Step 2: Make Gradlew Executable (Linux/Mac only)
```bash
chmod +x gradlew
```

### Step 3: Build Debug APK
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 4: Build Release APK (Optional)
For production release:
```bash
# Windows
gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

## Installing on Android Device

### Method 1: Using ADB (Android Debug Bridge)

1. Enable Developer Options on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings > Developer Options
   - Enable "USB Debugging"

2. Connect device via USB

3. Install APK:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Method 2: Manual Installation

1. Copy `app-debug.apk` to your Android device
2. Open the file on your device
3. Allow installation from unknown sources if prompted
4. Tap "Install"

## Troubleshooting

### Issue: "ANDROID_HOME not set"
**Solution**: Set the ANDROID_HOME environment variable as shown in Prerequisites

### Issue: "SDK location not found"
**Solution**: Create `local.properties` file in project root:
```properties
sdk.dir=C\:\\Android
```
(Use forward slashes on Linux/Mac: `sdk.dir=/home/user/Android`)

### Issue: "Gradle build failed"
**Solution**: 
1. Clean the project: `gradlew clean`
2. Try building again: `gradlew assembleDebug`

### Issue: "Out of memory"
**Solution**: Increase Gradle memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## Building Without Internet (Offline Mode)

If you have all dependencies cached:
```bash
gradlew assembleDebug --offline
```

## Verifying the Build

Check APK details:
```bash
# Windows
gradlew.bat signingReport

# Linux/Mac
./gradlew signingReport
```

## Running Tests

```bash
# Unit tests
gradlew test

# Android instrumentation tests (requires connected device/emulator)
gradlew connectedAndroidTest
```

## Creating Signed Release APK

### Step 1: Generate Keystore
```bash
keytool -genkey -v -keystore anc-release-key.keystore -alias anc-key -keyalg RSA -keysize 2048 -validity 10000
```

### Step 2: Create `keystore.properties` in project root
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=anc-key
storeFile=../anc-release-key.keystore
```

### Step 3: Update `app/build.gradle`
Add before `android` block:
```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}
```

Add inside `android` block:
```gradle
signingConfigs {
    release {
        keyAlias keystoreProperties['keyAlias']
        keyPassword keystoreProperties['keyPassword']
        storeFile file(keystoreProperties['storeFile'])
        storePassword keystoreProperties['storePassword']
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

### Step 4: Build Signed Release
```bash
gradlew assembleRelease
```

Signed APK location:
```
app/build/outputs/apk/release/app-release.apk
```

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/studio/build/building-cmdline)
- [Gradle Build Tool](https://gradle.org/guides/)
- [ADB Commands Reference](https://developer.android.com/studio/command-line/adb)

## Quick Reference Commands

```bash
# Clean build
gradlew clean

# Build debug APK
gradlew assembleDebug

# Build release APK
gradlew assembleRelease

# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall from device
adb uninstall com.anc.ruralhealth

# View connected devices
adb devices

# View app logs
adb logcat | grep "ANC"
```

## Support

For issues or questions, refer to the main README.md file or Android developer documentation.