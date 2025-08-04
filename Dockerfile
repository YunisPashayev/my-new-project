FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY build/libs/ecommerce-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]