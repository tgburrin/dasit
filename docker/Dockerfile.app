FROM gradle:7.2.0-jdk11 AS build
ENV SPRING_PROFILES_ACTIVE=docker
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM alpine:3.13 AS app
RUN apk update
RUN apk add strace gradle bash vim openjdk11-jdk
COPY --from=build /home/gradle/src/build/libs/dasit-0.0.2-SNAPSHOT.jar /tmp
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["/usr/bin/java", "-jar", "/tmp/dasit-0.0.2-SNAPSHOT.jar"]
