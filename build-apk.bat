@echo off
echo ========================================
echo ANC Rural Health - APK Build Script
echo ========================================
echo.

REM Check if gradlew exists
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found!
    echo Please make sure you're running this from the project root directory.
    pause
    exit /b 1
)

echo Step 1: Cleaning previous builds...
call gradlew.bat clean
if errorlevel 1 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Building debug APK...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo ERROR: Build failed!
    echo.
    echo Common issues:
    echo - ANDROID_HOME not set
    echo - Java JDK not installed
    echo - Android SDK not installed
    echo.
    echo Please check BUILD_INSTRUCTIONS.md for setup help.
    pause
    exit /b 1
)

echo.
echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo.
echo APK Location:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo File size:
for %%A in (app\build\outputs\apk\debug\app-debug.apk) do echo %%~zA bytes
echo.
echo You can now install this APK on your Android device.
echo.
pause

@REM Made with Bob
