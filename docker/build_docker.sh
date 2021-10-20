#!/usr/bin/env bash

DOCKER_BASE="${PWD}/`dirname $0`"

cd $DOCKER_BASE

docker build .. -f Dockerfile.build -t dasit_build
docker run --rm -v "${PWD}/../:/application_source" -u $(id -u):$(id -g) -ti dasit_build

docker build .. -f Dockerfile.app -t dasit-app
docker build .. -f Dockerfile.pg -t dasit-postgresql

docker-compose up
