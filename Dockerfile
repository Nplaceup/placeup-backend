FROM gradle:8.13-jdk21 AS build

ARG MODULE

WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY core core
COPY api api
COPY admin admin
COPY scheduler scheduler

RUN gradle :${MODULE}:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre

ARG MODULE

WORKDIR /app

COPY --from=build /workspace/${MODULE}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
