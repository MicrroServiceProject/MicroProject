# Build stage
FROM maven:3.8.7-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
