# ==============================================================================
# Stage 1: Standard Build using Maven + Java 26
# ==============================================================================
FROM maven:3.9-eclipse-temurin-26-noble AS builder
WORKDIR /build

# Copy Maven dependency configuration file first to leverage layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual application source code and compile the standard JAR file
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# Stage 2: Ultra-low memory, secure JRE 26 runtime container
# ==============================================================================
FROM eclipse-temurin:26-jre-noble
WORKDIR /app

# Create a secure, non-root system group and user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the compiled executable fat JAR from Stage 1
COPY --from=builder /build/target/*.jar app.jar

# Expose your application port
EXPOSE 8082

# Production cloud environmental configurations
# We use SerialGC and aggressive memory flags to force Java 26 to run on minimal RAM.
ENV SERVER_PORT=8082 \
    SPRING_APPLICATION_NAME=ai-scheduler \
    SPRING_PROFILES_ACTIVE=openai \
    AI_SCHEDULER_SPEECH_ENABLED=false \
    JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -XX:MaxRAMPercentage=70.0 -XX:MinRAMPercentage=50.0 -XX:TieredStopAtLevel=1 -Dspring.main.lazy-initialization=true"

# Boot the application cleanly using the optimized memory properties
ENTRYPOINT ["java", "-jar", "app.jar"]
