# Use the official OpenJDK 21 image
FROM maven:3.9-eclipse-temurin-21 AS builder

LABEL maintainer="Daria Lyazgina <daria.lyazgina@gmail.com>"
LABEL author="Daria Lyazgina"
LABEL email="daria.lyazgina@gmail.com"
LABEL creators="Daria Lyazgina"
LABEL org.opencontainers.image.authors="Daria Lyazgina <daria.lyazgina@gmail.com>"
LABEL com.docker.desktop.creator="Daria Lyazgina"
LABEL version="1.0"
LABEL description="Calculator Server Application"

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
LABEL auto-remove="true"
WORKDIR /app
COPY --from=builder /app/target/calcclient-1.0-SNAPSHOT.jar app.jar

# Точка входа - запускаем скопированный JAR
CMD ["java", "-jar", "app.jar", "Client" ]