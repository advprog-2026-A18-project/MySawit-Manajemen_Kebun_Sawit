FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app


COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .


RUN chmod +x ./gradlew


COPY src ./src


RUN ./gradlew clean bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG DATABASE_URL

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082

ENV DATABASE_URL=${DATABASE_URL}

ENTRYPOINT ["java", "-jar", "app.jar"]
