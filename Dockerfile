# Step 1 : Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2 : Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Création of a no root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar incident-tracker.jar
ENTRYPOINT ["java", "-jar", "incident-tracker.jar"]