@echo off
title Building Executable JAR - Snake Game Pro
echo =======================================
echo     SNAKE GAME PRO - PACKAGING SCRIPT
echo =======================================
echo.
echo [1/3] Compiling Java classes...
if not exist bin mkdir bin
javac -d bin -cp "lib/*" src/Main.java src/controller/*.java src/database/*.java src/model/*.java src/utils/*.java src/view/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo [2/3] Generating Manifest file...
echo Manifest-Version: 1.0 > manifest.txt
echo Main-Class: Main >> manifest.txt
echo Class-Path: lib/mysql-connector-j-8.4.0.jar >> manifest.txt
echo. >> manifest.txt

echo [3/3] Creating executable JAR (SnakeGame.jar)...
jar cfm SnakeGame.jar manifest.txt -C bin .
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to package JAR!
    del manifest.txt
    pause
    exit /b %errorlevel%
)

del manifest.txt
echo.
echo =======================================
echo [SUCCESS] Executable JAR created: SnakeGame.jar
echo Note: The JAR requires the "lib/" folder to be adjacent to work with MySQL.
echo =======================================
pause
