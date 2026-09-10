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
echo    OIBSIP Task 4 - Quiz Desktop Application
echo =========================================================
echo Compiling and starting Desktop Quiz GUI...

"%JAVAC_EXE%" -d "target_cli_classes" app/src/main/java/com/oibsip/quiz/model/*.java app/src/main/java/com/oibsip/quiz/data/*.java app/src/main/java/com/oibsip/quiz/engine/*.java app/src/test/java/com/oibsip/quiz/DesktopQuizApp.java

if errorlevel 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

start "" "%JAVA_EXE%" -cp "target_cli_classes" com.oibsip.quiz.DesktopQuizApp
echo [SUCCESS] Quiz window launched!
