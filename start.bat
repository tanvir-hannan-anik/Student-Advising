@echo off
title Student Advising Portal - Startup

echo ================================================
echo   Student Advising Portal - Starting Services
echo ================================================
echo.

REM ── Check Python ────────────────────────────────
python --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Python not found. Please install Python 3.8+
    pause
    exit /b 1
)

REM ── Install Python dependencies if needed ────────
echo [1/3] Checking Python dependencies...
pip install -r ai-service\requirements.txt -q
echo       Done.

REM ── Start Flask AI service in background ─────────
echo [2/3] Starting AI service on http://localhost:5000 ...
start "AI Service (Flask)" cmd /k "cd /d "%~dp0ai-service" && python app.py"

REM ── Wait a moment for Flask to initialise ────────
timeout /t 3 /nobreak >nul

REM ── Start Spring Boot ────────────────────────────
echo [3/3] Starting Spring Boot app on http://localhost:8080 ...
echo.
echo ================================================
echo   Open your browser at: http://localhost:8080
echo   Close this window to stop Spring Boot.
echo   Close the "AI Service" window to stop the AI.
echo ================================================
echo.
mvn spring-boot:run

pause
