# Этап сборки
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
# Скачивание зависимостей перед сборкой образа
RUN mvn dependency:go-offline
COPY src ./src
# Сборка проекта
RUN mvn package -DskipTests

# Этап развертывания
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/target/info-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
CMD ["java", "-jar", "info-service-0.0.1-SNAPSHOT.jar"]