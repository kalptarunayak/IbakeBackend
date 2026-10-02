# ==============================================================================
# Dockerfile for IBake Spring Boot REST Backend
# Optimized for Render Web Service Deployment (render.com)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build the Spring Boot application using Maven
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Layer caching: Copy pom.xml first to resolve and cache dependencies
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copy backend source code and compile production JAR
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Production JRE runtime image (Ubuntu Jammy based Temurin 17 JRE)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install curl for container health monitoring (used by Render / orchestration)
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Security: Run as a non-privileged system user
RUN groupadd -r ibake && useradd -r -g ibake -s /bin/false ibake

# Copy the built jar from the build stage
COPY --from=build /workspace/target/ibake-backend-*.jar app.jar

# Set ownership
RUN chown -R ibake:ibake /app
USER ibake

# Render dynamically injects $PORT (default is 10000 on Render web services)
ENV PORT=10000
EXPOSE ${PORT}

# Health check endpoint via Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# Optimized JVM settings for Render containers (including 512MB RAM free/starter tiers):
# - XX:+UseContainerSupport: Respect cgroup memory limits
# - XX:MaxRAMPercentage=75.0: Allocate up to 75% of container RAM to JVM heap
# - XX:InitialRAMPercentage=40.0: Conservative initial heap allocation
# - Xss512k: Reduce thread stack size to conserve memory
# - XX:+ExitOnOutOfMemoryError: Fast failover if memory exhausted
# - Dserver.port=${PORT}: Guarantee binding to Render's dynamic port
ENTRYPOINT ["sh", "-c", "exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=40.0 -XX:+ExitOnOutOfMemoryError -Xss512k -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar app.jar"]
