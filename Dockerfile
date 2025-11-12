# Use an official Java runtime
FROM eclipse-temurin:25-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR built by Maven
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
