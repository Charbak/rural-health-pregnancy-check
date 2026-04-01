# Quick Start Guide - Building Your APK

Since I cannot directly build the APK for you, here's the fastest way to get your APK:

## Option 1: Use Online Build Service (Easiest - No Setup Required)

### Using GitHub Actions (Recommended)
1. Create a GitHub account if you don't have one
2. Create a new repository
3. Upload all project files to the repository
4. Create `.github/workflows/build.yml`:

```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        
      - name: Build with Gradle
        run: ./gradlew assembleDebug
        
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

5. Push to GitHub - APK will be built automatically
6. Download APK from Actions tab → Artifacts

## Option 2: Use Your Local Machine (Requires Setup)

### Prerequisites Installation (One-time setup):

#### Step 1: Install Java JDK 17
**Windows:**
1. Download from: https://adoptium.net/temurin/releases/?version=17
2. Run installer
3. Add to PATH:
   - Search "Environment Variables" in Windows
   - Add `C:\Program Files\Eclipse Adoptium\jdk-17.x.x\bin` to PATH

**Verify:**
```bash
java -version
```

#### Step 2: Install Android Command Line Tools
1. Download: https://developer.android.com/studio#command-tools
2. Extract to `C:\Android\cmdline-tools\latest`
3. Set environment variables:
   ```powershell
   setx ANDROID_HOME "C:\Android"
   setx PATH "%PATH%;C:\Android\cmdline-tools\latest\bin"
   ```
4. Install SDK components:
   ```bash
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   sdkmanager --licenses
   ```

### Build the APK:

**Method A: Using the build script (Easiest)**
```bash
cd "c:\Users\CHARBAKROY\Desktop\Mobile App"
build-apk.bat
```

**Method B: Manual command**
```bash
cd "c:\Users\CHARBAKROY\Desktop\Mobile App"
gradlew.bat assembleDebug
```

**APK Location:**
```
app\build\outputs\apk\debug\app-debug.apk
```

## Option 3: Use Android Studio (Most User-Friendly)

1. Download Android Studio: https://developer.android.com/studio
2. Install Android Studio
3. Open project: File → Open → Select "Mobile App" folder
4. Wait for Gradle sync to complete
5. Build → Build Bundle(s) / APK(s) → Build APK(s)
6. Click "locate" in the notification to find APK

## Option 4: Use Online IDE (No Installation)

### Replit (Free)
1. Go to https://replit.com
2. Create new Repl → Import from GitHub
3. Upload your project files
4. Run build command in shell
5. Download APK from file explorer

### Gitpod (Free)
1. Go to https://gitpod.io
2. Create workspace from GitHub repo
3. Run: `./gradlew assembleDebug`
4. Download APK

## Troubleshooting

### "ANDROID_HOME not set"
Create `local.properties` in project root:
```properties
sdk.dir=C\:\\Android
```

### "Java not found"
Install JDK 17 and add to PATH

### "Gradle build failed"
1. Clean: `gradlew clean`
2. Try again: `gradlew assembleDebug`

### "Out of memory"
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

## Installing APK on Android Device

1. Copy `app-debug.apk` to your phone
2. Open the file
3. Allow "Install from Unknown Sources" if prompted
4. Tap Install

## Need Help?

If you're still having trouble:
1. Check BUILD_INSTRUCTIONS.md for detailed steps
2. Ensure all prerequisites are installed
3. Try the GitHub Actions method (easiest, no local setup)

## Estimated Build Time
- First build: 5-10 minutes (downloads dependencies)
- Subsequent builds: 1-2 minutes

## APK Size
Expected size: ~10-15 MB