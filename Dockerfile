# Build Java Application Only
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests -q

# Runtime - Java only
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/target/admin-portal-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java"]
CMD ["-Dserver.port=8080", "-jar", "app.jar"]
