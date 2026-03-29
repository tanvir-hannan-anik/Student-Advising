# Multi-stage build for Java application
FROM maven:3.9.6-eclipse-temurin-17 as java-builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -q -DskipTests

# Final image with Java and Python
FROM eclipse-temurin:17-jre-jammy

# Install Python and required packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3.11 \
    python3-pip \
    && pip install --upgrade pip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built Java application
COPY --from=java-builder /build/target/admin-portal-0.0.1-SNAPSHOT.jar .

# Copy Python AI service
COPY ai-service ./ai-service
COPY start-services.sh .

# Install Python dependencies
RUN pip install --no-cache-dir -r ai-service/requirements.txt

# Make script executable
RUN chmod +x start-services.sh

# Expose ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/api/admin-systems/overview || exit 1

# Start both services
CMD ["./start-services.sh"]
