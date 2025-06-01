FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy gradle files first for better layer caching
COPY gradle/ gradle/
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create a directory for the SQLite database
RUN mkdir -p /data

# Set environment variable for the database location
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/todo.db

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]