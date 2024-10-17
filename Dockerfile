# Используем официальный образ OpenJDK в качестве базового
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar файл в контейнер
COPY build/libs/TelegaBot-0.1-all.jar /app/app.jar

# Запускаем приложение
CMD ["java", "-jar", "/app/app.jar"]

