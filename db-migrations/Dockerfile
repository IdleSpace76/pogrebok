FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY . .
RUN mvn -q -DskipTests -pl db-migrations -am package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /src/db-migrations/target/*-exec.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]


