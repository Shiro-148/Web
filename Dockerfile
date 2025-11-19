
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

RUN mvn -q -B dependency:go-offline

COPY src ./src

RUN mvn -q -B clean package -DskipTests


FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/web-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=default

ENTRYPOINT ["java","-jar","/app/app.war"]

