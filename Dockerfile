# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir -p /app/logs
COPY --from=build /app/target/library-management-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 1212
ENTRYPOINT ["java", "-jar", "app.jar"]

