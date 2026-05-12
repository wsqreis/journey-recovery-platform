FROM gradle:9.1.0-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :apps:decision-worker:bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/apps/decision-worker/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
