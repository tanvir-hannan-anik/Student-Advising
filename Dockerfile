# Build Java application
FROM maven:3.9.6-eclipse-temurin-17 as java-builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime image with both Java and Python
FROM eclipse-temurin:17-jre

# Install Python
RUN apt-get update && apt-get install -y \
    python3.11 \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built Java JAR
COPY --from=java-builder /app/target/admin-portal-0.0.1-SNAPSHOT.jar .

# Copy Python AI service
COPY ai-service ./ai-service
WORKDIR /app/ai-service

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

WORKDIR /app

# Expose ports
EXPOSE 8080 5000

# Start script
COPY start-services.sh .
RUN chmod +x start-services.sh

CMD ["./start-services.sh"]
