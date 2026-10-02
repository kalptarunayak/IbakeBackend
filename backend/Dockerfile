# Stage 1: Build the application with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy dependency definition first for caching layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create non-root system user for security
RUN groupadd -r ibake && useradd -r -g ibake ibake

# Copy the built jar from the build stage
COPY --from=build /app/target/ibake-backend-*.jar app.jar

RUN chown -R ibake:ibake /app
USER ibake

# Railway dynamically provides PORT environment variable
ENV PORT=8080
EXPOSE ${PORT}

# JVM tuning for containerized memory limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
