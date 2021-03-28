FROM gradle:6.8.3-jdk11 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build -x test

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar yumme.jar
CMD java -jar yumme.jar
