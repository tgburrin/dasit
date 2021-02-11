FROM alpine:3.13

RUN apk update
RUN apk add strace gradle bash vim openjdk11-jdk

COPY build/libs/dasit-0.0.1-SNAPSHOT.jar /tmp

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["/usr/bin/java", "-jar", "/tmp/dasit-0.0.1-SNAPSHOT.jar"]
