FROM openjdk:8

LABEL authors="dsousasantos91"

VOLUME /tmp

RUN apt-get update && apt-get install -y wget

COPY ./target/ms-assembleia-1.0.0-SNAPSHOT.jar ms-assembleia.jar

CMD ["java","-jar","/ms-assembleia.jar"]
