# Этап сборки
# Используем официальный образ Maven с OpenJDK 17 для сборки приложения
FROM maven:3.8.6 AS build

# Устанавливаем рабочую директорию для проекта
WORKDIR /app

# Копируем pom.xml и скачиваем все зависимости без сборки
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем весь проект в контейнер и выполняем сборку
COPY src ./src
RUN mvn clean package -DskipTests

# Этап выполнения
# Используем легковесный образ OpenJDK для запуска приложения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию для запуска приложения
WORKDIR /app

# Копируем скомпилированный JAR-файл из этапа сборки
COPY --from=build /app/target/WEB-ZAIM-1.0-SNAPSHOT.jar app.jar

# Устанавливаем переменные окружения для базы данных (можно изменить в docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/databasefortesttask
ENV SPRING_DATASOURCE_USERNAME=userfortesttask
ENV SPRING_DATASOURCE_PASSWORD=0000
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Открываем порт 8080 для приложения
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
