#!/bin/bash

# Start Python Flask AI service in background
cd /app/ai-service
python app.py &
AI_PID=$!

# Start Java Spring Boot application
cd /app
java -jar admin-portal-0.0.1-SNAPSHOT.jar

# Kill the Python service when Java app stops
kill $AI_PID
