@echo off
REM ============================================================
REM build.bat – Windows build script for IMS (Maven)
REM Usage: build.bat
REM ============================================================

cd /d C:\Users\LOQ\IdeaProjects\Java-Invent

REM Check for Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven not found.
    echo Install Maven from https://maven.apache.org/download.cgi
    echo and add 'bin' folder to your system PATH.
    pause
    exit /b 1
)

REM Build and run
echo Building with Maven...
mvn clean compile exec:java
