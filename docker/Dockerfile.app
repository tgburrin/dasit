FROM alpine:3.13

RUN apk update
RUN apk add strace gradle bash vim openjdk11-jdk

ENV SPRING_PROFILES_ACTIVE=docker

ADD . /application_source

WORKDIR /application_source

RUN gradle build -x test

ENTRYPOINT ["/usr/bin/java", "-jar", "/application_source/build/libs/dasit-0.0.1-SNAPSHOT.jar"]
