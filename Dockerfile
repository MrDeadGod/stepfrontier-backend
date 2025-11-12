# Use an OpenJDK base image
FROM eclipse-temurin:25-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the Spring Boot JAR
RUN ./mvnw clean package -DskipTests

# Copy the JAR to app folder
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java","-jar","app.jar"]
