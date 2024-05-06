FROM openjdk:17-alpine as builder
WORKDIR /app
COPY ./target/info-service-0.0.1-SNAPSHOT.jar /app/

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/info-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
CMD ["java", "-jar", "info-service-0.0.1-SNAPSHOT.jar"]
