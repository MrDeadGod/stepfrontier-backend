# Step 1: Build the JAR using Maven
FROM maven:3.9.4-eclipse-temurin-25 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Step 2: Run the JAR using a lightweight JDK
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
