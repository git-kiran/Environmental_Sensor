# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file (Make sure you build the JAR first using `mvn package`)
COPY target/central-monitoring-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
