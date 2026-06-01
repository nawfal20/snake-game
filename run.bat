@echo off
title Running Snake Game Pro...
echo =======================================
echo     SNAKE GAME PRO - STARTUP SCRIPT
echo =======================================
echo.
echo [1/2] Compiling files...
if not exist bin mkdir bin
javac -d bin -cp "lib/*" src/Main.java src/controller/*.java src/database/*.java src/model/*.java src/utils/*.java src/view/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check your JDK installation.
    pause
    exit /b %errorlevel%
)

echo [2/2] Launching game...
java -cp "bin;lib/*" Main
if %errorlevel% neq 0 (
    echo.
    echo [WARNING] Game closed with an error. If database connection failed,
    echo           the game can still run in local guest mode!
    pause
)
