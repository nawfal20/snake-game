@echo off
title Snake Game Pro
cd /d "%~dp0"

:: Check if JAR exists
if exist SnakeGame.jar (
    start javaw -jar SnakeGame.jar
    exit
)

:: Check if pre-compiled bin classes exist
if exist bin\Main.class (
    start javaw -cp "bin;lib/*" Main
    exit
)

:: If nothing is compiled, try compiling (fallback for developers)
echo [INFO] Premier lancement : Compilation des sources Java...
if not exist bin mkdir bin
javac -d bin -cp "lib/*" src/Main.java src/controller/*.java src/database/*.java src/model/*.java src/utils/*.java src/view/*.java

if %errorlevel% equ 0 (
    start javaw -cp "bin;lib/*" Main
) else (
    echo.
    echo [ERREUR] La compilation a echoue.
    echo Veuillez verifier que le JDK Java 17 ou superieur est installe sur votre systeme.
    echo.
    pause
)
