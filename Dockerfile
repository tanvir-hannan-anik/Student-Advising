# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -X 2>&1 | tail -50

# Check if JAR was created
RUN ls -la target/ || echo "Build directory not found"

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install Python
RUN apt-get update && \
    apt-get install -y --no-install-recommends python3.11 python3-pip curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/admin-portal-0.0.1-SNAPSHOT.jar app.jar

# Copy Python service
COPY ai-service ./ai-service

# Install Python dependencies
RUN pip install --no-cache-dir -r ai-service/requirements.txt

# Copy startup script
COPY start-services.sh .
RUN chmod +x start-services.sh

EXPOSE 8080 5000

# Start services
CMD ["./start-services.sh"]
