# Use Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK for the runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the previous build stage
COPY --from=build /app/target/MD-0.0.1-SNAPSHOT.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
