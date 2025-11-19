### Multi-stage Dockerfile for Spring Boot WAR (Java 21)

# ====== BUILD STAGE ======
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven wrapper and pom first to leverage layer caching
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

# Download dependencies (no source yet, better cache)
RUN mvn -q -B dependency:go-offline

# Copy source code
COPY src ./src

# Build the project (skip tests for faster image build; change if needed)
RUN mvn -q -B clean package -DskipTests


# ====== RUNTIME STAGE ======
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy built WAR from build stage
COPY --from=build /app/target/web-0.0.1-SNAPSHOT.war app.war

# Expose application port
EXPOSE 8080

# Default active profile can be overridden at runtime
ENV SPRING_PROFILES_ACTIVE=default

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.war"]

