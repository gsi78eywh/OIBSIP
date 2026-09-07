@echo off
setlocal
set "JAVA_EXE=C:\oracleJdk-26\bin\java.exe"
set "JAVAC_EXE=C:\oracleJdk-26\bin\javac.exe"

if not exist "%JAVA_EXE%" (
    set "JAVA_EXE=java"
    set "JAVAC_EXE=javac"
)

if not exist "target_cli_classes" mkdir "target_cli_classes"

echo [1/2] Compiling Unit Converter Application...
"%JAVAC_EXE%" -d "target_cli_classes" app/src/main/java/com/oibsip/unitconverter/model/*.java app/src/main/java/com/oibsip/unitconverter/converter/*.java app/src/test/java/com/oibsip/unitconverter/DesktopUnitConverterApp.java app/src/test/java/com/oibsip/unitconverter/InteractiveConsoleRunner.java app/src/test/java/com/oibsip/unitconverter/UnitConverterVerification.java

if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

echo [2/2] Launching Unit Converter App...
start "OIBSIP Unit Converter" "%JAVA_EXE%" -cp "target_cli_classes" com.oibsip.unitconverter.DesktopUnitConverterApp
echo App launched successfully!
