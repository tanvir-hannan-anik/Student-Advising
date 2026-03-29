#!/bin/bash
set -e

echo "=== Starting Academic Admin & Advising Portal ==="

# Start Python Flask AI service in background
echo "Starting Python AI Service on port 5000..."
cd /app/ai-service
python app.py > /tmp/python.log 2>&1 &
AI_PID=$!
echo "Python service PID: $AI_PID"

# Give Python service time to start
sleep 2

# Start Java Spring Boot application
echo "Starting Java Application on port 8080..."
cd /app
java -Dserver.port=8080 -jar app.jar

# Cleanup
kill $AI_PID 2>/dev/null || true
