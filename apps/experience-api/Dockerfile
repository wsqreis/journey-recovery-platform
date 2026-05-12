FROM gradle:9.1.0-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :apps:experience-api:bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/apps/experience-api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
