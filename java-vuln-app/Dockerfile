FROM maven:3.6-jdk-8-slim AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage - using Red Hat UBI with Java 8
FROM registry.access.redhat.com/ubi8/openjdk-8:latest

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment
ENV PORT=8080

# Run application
CMD ["java", "-jar", "app.jar"]

# Made with Bob
