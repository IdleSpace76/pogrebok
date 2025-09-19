FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY . .
RUN mvn -q -DskipTests -pl node -am package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /src/node/target/*-exec.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]


