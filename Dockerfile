# ==============================================================
# Stage 1: Build Stage
# Uses Maven + JDK 21 to compile and package the application
# ==============================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and POM first (layer caching for dependencies)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached unless pom.xml changes)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build the fat JAR (skip tests for faster builds)
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ==============================================================
# Stage 2: Runtime Stage
# Uses a slim JRE 21 image — smaller footprint, faster startup
# ==============================================================
FROM eclipse-temurin:21-jre AS runtime

# Create a non-root user for security best practices
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /app

# Copy only the packaged JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Give ownership to the non-root user
RUN chown appuser:appgroup app.jar

USER appuser

# Render dynamically assigns a PORT environment variable — expose it
EXPOSE 8080

# JVM tuning for containerized environments:
#   -XX:+UseContainerSupport  → respect container CPU/memory limits
#   -XX:MaxRAMPercentage=75.0 → use up to 75% of container RAM for heap
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
