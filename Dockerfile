FROM gradle:8.2-jdk17 AS build
COPY . /home/app
WORKDIR /home/app
RUN gradle :api:shadowJar --no-daemon

FROM eclipse-temurin:17-jre
COPY --from=build /home/app/api/build/libs/api-1.0.0-all.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]