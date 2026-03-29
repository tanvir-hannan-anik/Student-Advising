#!/bin/bash

echo "Starting Academic Admin & Advising Portal..."

# Start Python Flask AI service in background
cd /app/ai-service
python app.py &
sleep 2

# Start Java application
cd /app
java -Dserver.port=${PORT:-8080} -jar admin-portal-0.0.1-SNAPSHOT.jar
