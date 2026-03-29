# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copy everything
COPY . .

# Build - verbose output for debugging
RUN mvn -DskipTests -B clean package 2>&1 | grep -E "BUILD|ERROR|SUCCESS" || true

# Check build result
RUN ls -la target/*.jar || echo "BUILD FAILED - JAR not found"

# Stage 2: Run
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=0 /app/target/admin-portal-0.0.1-SNAPSHOT.jar application.jar

# Port
EXPOSE 8080

# Start app with explicit settings
CMD ["java", "-Xmx256m", "-Dserver.port=8080", "-Dspring.jpa.hibernate.ddl-auto=update", "-jar", "application.jar"]
