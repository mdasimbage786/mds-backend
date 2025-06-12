# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Create a directory for the app
WORKDIR /app

# Copy the jar file into the image
COPY target/MD-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
