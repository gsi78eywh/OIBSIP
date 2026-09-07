@echo off
setlocal

set "JAVA_EXE=C:\oracleJdk-26\bin\java.exe"
set "JAVAC_EXE=C:\oracleJdk-26\bin\javac.exe"

if not exist "%JAVA_EXE%" (
    set "JAVA_EXE=java"
    set "JAVAC_EXE=javac"
)

if not exist "target_cli_classes" mkdir "target_cli_classes"

echo =========================================================
echo    OIBSIP Task 5 - Stopwatch Desktop Application
echo =========================================================
echo Compiling and starting Desktop Stopwatch GUI...

"%JAVAC_EXE%" -d "target_cli_classes" app/src/main/java/com/oibsip/stopwatch/model/*.java app/src/main/java/com/oibsip/stopwatch/engine/*.java app/src/test/java/com/oibsip/stopwatch/DesktopStopwatchApp.java

if errorlevel 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

start "" "%JAVA_EXE%" -cp "target_cli_classes" com.oibsip.stopwatch.DesktopStopwatchApp
echo [SUCCESS] Stopwatch window launched!
