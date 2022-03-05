FROM gradle:7.4-jdk17-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build -x test

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar yumme-server.jar
CMD java -jar yumme-server.jar
