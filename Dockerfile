# Simple Java Build
FROM maven:3.9.6-eclipse-temurin-17 as maven-builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y python3.11 python3-pip && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR
COPY --from=maven-builder /build/target/admin-portal-0.0.1-SNAPSHOT.jar .

# Copy Python
COPY ai-service ./ai-service
RUN pip install -r ai-service/requirements.txt

COPY start-services.sh .
RUN chmod +x start-services.sh

EXPOSE 8080 5000
CMD ["./start-services.sh"]
