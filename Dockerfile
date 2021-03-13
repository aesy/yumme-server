FROM openjdk:alpine

RUN mkdir /app
WORKDIR /app
ADD build/libs/Yumme-0.0.1-SNAPSHOT.jar recipe.jar

CMD java -jar recipe.jar
