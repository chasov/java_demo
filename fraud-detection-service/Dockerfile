FROM maven:3.9.8-amazoncorretto-17 as build

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk-slim

WORKDIR /fraud-service

COPY --from=build target/demo-0.0.1-SNAPSHOT.jar ./java-app.jar


ENTRYPOINT ["java", "-jar", "./java-app.jar"]