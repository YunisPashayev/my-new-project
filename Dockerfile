FROM gradle:8.4-jdk17 AS builder
COPY . /home/app
WORKDIR /home/app
RUN gradle clean build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]