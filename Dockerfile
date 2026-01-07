FROM eclipse-temurin:17-jdk-jammy

# Install curl and other utilities
RUN apt-get update && \
    apt-get install -y curl iputils-ping dnsutils netcat && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Expose the port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


# # Build stage
# FROM maven:3.9-eclipse-temurin-17-alpine AS build
# WORKDIR /app

# # Copy the POM file first to leverage Docker cache
# COPY pom.xml .
# # Download all required dependencies into one layer
# RUN mvn dependency:go-offline -B

# # Copy source code
# COPY src ./src

# # Build the application
# RUN mvn package -DskipTests
# RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# # Production stage
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# # Add a non-root user to run the app
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

# # Copy the built artifact from the build stage
# COPY --from=build /app/target/*.jar app.jar

# # Set environment variables
# ENV JAVA_OPTS="-Xms512m -Xmx512m"
# ENV SPRING_PROFILES_ACTIVE="prod"

# # Expose the application port
# EXPOSE 9090

# # Run the application
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
