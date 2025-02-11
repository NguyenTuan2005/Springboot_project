# Stage 1: Build the project using Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Now copy the source code and build the JAR
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim
VOLUME /tmp
# Copy the generated JAR from the build stage. Adjust the JAR file name if needed.
COPY --from=build /app/target/task-management-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
