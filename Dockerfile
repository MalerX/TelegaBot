# Этап сборки
FROM gradle:8.10.2-jdk21 AS build

# Устанавливаем рабочую директорию для сборки
WORKDIR /build

# Копируем файл сборки в контейнер
COPY . /build

# Пример команды сборки проекта с использованием Gradle
# (Предполагается, что вы используете Gradle, замените на вашу команду сборки, если нужно)
RUN ./gradlew build

# Этап выполнения
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию для выполнения
WORKDIR /app

# Копируем собранный jar файл из этапа сборки
COPY --from=build /build/build/libs/TelegaBot-0.1-all.jar /app/app.jar

# Запускаем приложение с заданными параметрами кучи памяти
CMD ["java", "-Xms1g", "-Xmx3g", "-jar", "/app/app.jar"]
