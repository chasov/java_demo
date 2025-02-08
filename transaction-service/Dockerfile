FROM maven:3.9.8-amazoncorretto-17 as build

# Копируем исходный код и файл pom.xml
COPY src src
COPY pom.xml pom.xml

# Сборка приложения без тестов
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR-файл из первого этапа
COPY --from=build target/t1-java-demo-0.0.1-SNAPSHOT.jar ./java-app.jar

# Открываем порт, если необходимо

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "./java-app.jar"]