# ================================================
# Dockerfile for StudentTap NFC Portfolio
# Spring Boot 4.0.3 + Java 17
# ================================================

# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create uploads directory
RUN mkdir -p /app/uploads/resumes
RUN mkdir -p /app/uploads/gallery
RUN mkdir -p /app/uploads/profiles
RUN mkdir -p /app/uploads/covers

# Copy JAR from build stage
COPY --from=build /app/target/student-portfolio1-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-Xmx400m", "-Xms200m","-XX:+UseSerialGC", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]