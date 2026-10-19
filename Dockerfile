# Multi-stage Dockerfile for Spring Boot on Render
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy dependency definition and source
COPY pom.xml .
COPY src ./src

# Build JAR package skipping tests
RUN mvn clean package -DskipTests

# Runtime stage with lightweight Alpine JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from builder
COPY --from=build /app/target/*.jar app.jar

# Render dynamic port
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
