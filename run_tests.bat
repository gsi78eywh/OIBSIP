@echo off
setlocal enabledelayedexpansion

set "JAVA_EXE=C:\oracleJdk-26\bin\java.exe"
set "JAVAC_EXE=C:\oracleJdk-26\bin\javac.exe"

if not exist "%JAVA_EXE%" (
    set "JAVA_EXE=java"
    set "JAVAC_EXE=javac"
)

set "JUNIT_JAR=C:\Users\SethAndreyJabagat\.gradle\wrapper\dists\gradle-8.5-bin\5t9huq95ubn472n8rpzujfbqh\gradle-8.5\lib\junit-4.13.2.jar"
set "HAMCREST_JAR=C:\Users\SethAndreyJabagat\.gradle\wrapper\dists\gradle-8.5-bin\5t9huq95ubn472n8rpzujfbqh\gradle-8.5\lib\hamcrest-core-1.3.jar"

echo =========================================================
echo    OIBSIP Unit Converter - Automated Test Runner
echo =========================================================
echo Using Java: %JAVA_EXE%
echo.

if not exist "target_cli_classes" mkdir "target_cli_classes"

echo [1/3] Compiling source and test files...
"%JAVAC_EXE%" -cp "%JUNIT_JAR%;%HAMCREST_JAR%" -d "target_cli_classes" app/src/main/java/com/oibsip/unitconverter/model/*.java app/src/main/java/com/oibsip/unitconverter/converter/*.java app/src/test/java/com/oibsip/unitconverter/model/*.java app/src/test/java/com/oibsip/unitconverter/*.java app/src/main/java/com/oibsip/stopwatch/model/*.java app/src/main/java/com/oibsip/stopwatch/engine/*.java app/src/test/java/com/oibsip/stopwatch/*.java app/src/main/java/com/oibsip/quiz/model/*.java app/src/main/java/com/oibsip/quiz/data/*.java app/src/main/java/com/oibsip/quiz/engine/*.java app/src/test/java/com/oibsip/quiz/*.java

if errorlevel 1 (
    echo [ERROR] Compilation failed!
    exit /b 1
)
echo [SUCCESS] Compilation completed without errors.
echo.

echo [2/3] Running Comprehensive Verification Engine...
"%JAVA_EXE%" -cp "target_cli_classes" com.oibsip.unitconverter.UnitConverterVerification
if errorlevel 1 (
    echo [FAIL] Verification suite encountered failures.
    exit /b 1
)

echo.
echo [3/3] Running JUnit 4 Test Suite (Unit Converter + Stopwatch + Quiz App)...
"%JAVA_EXE%" -cp "target_cli_classes;%JUNIT_JAR%;%HAMCREST_JAR%" org.junit.runner.JUnitCore com.oibsip.unitconverter.model.CategoryTest com.oibsip.unitconverter.model.UnitTest com.oibsip.unitconverter.UnitConverterTest com.oibsip.unitconverter.InteractiveConsoleRunnerTest com.oibsip.stopwatch.StopwatchEngineTest com.oibsip.quiz.QuizEngineTest

if errorlevel 1 (
    echo [FAIL] JUnit tests failed.
    exit /b 1
)

echo.
echo =========================================================
echo  ALL TEST SUITES PASSED PERFECTLY (100%% SUCCESS)
echo =========================================================
exit /b 0
