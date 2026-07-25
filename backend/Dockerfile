# syntax=docker/dockerfile:1

# ---- Build stage: compile + run unit/slice tests (no external services needed) ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies first for faster rebuilds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# `package` runs Surefire (unit + @DataJpaTest/@WebMvcTest slices). Failsafe IT
# tests (Testcontainers) are NOT run here; they run via `mvn verify` on a host with Docker.
RUN mvn -B clean package

# ---- Runtime stage: small JRE image ----
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S fitmate && adduser -S fitmate -G fitmate
COPY --from=build /app/target/fitmate-backend.jar app.jar
USER fitmate

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
