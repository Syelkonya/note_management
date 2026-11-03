# syntax=docker/dockerfile:1

# =============================================================================
# Stage 1 - build the Spring Boot executable jar with the Maven wrapper
# =============================================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy the wrapper and the POM first so the dependency layer is cached and
# only invalidated when pom.xml (or the wrapper) changes.
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw \
    && ./mvnw -B -q -DskipTests dependency:go-offline

# Copy the sources only after the dependencies are cached, then package.
COPY src/ src/
RUN ./mvnw -B -DskipTests package

# =============================================================================
# Stage 2 - minimal runtime image running as a non-root user
# =============================================================================
FROM eclipse-temurin:17-jre-alpine

# Create an unprivileged system user and group for the application.
RUN addgroup -S appgroup \
    && adduser -S -G appgroup appuser

WORKDIR /app

# The Spring Boot Maven plugin produces exactly one executable jar in target/.
COPY --from=build --chown=appuser:appgroup /workspace/target/*.jar /app/app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
