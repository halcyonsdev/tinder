FROM gradle:8.5-jdk21 as builder

WORKDIR /app

COPY ../build.gradle.kts /app/
COPY ../settings.gradle.kts /app/

COPY . /app/

RUN gradle build --no-daemon

FROM openjdk:21-jdk-slim AS runtime

WORKDIR /app

COPY --from=builder /app/microservices/config-service/build/libs/config-service-1.0.0.jar /app/config-service.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/config-service.jar"]
