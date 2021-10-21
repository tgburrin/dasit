#!/usr/bin/env bash

DOCKER_BASE="${PWD}/`dirname $0`"

cd $DOCKER_BASE

docker build .. -f Dockerfile.app -t dasit-app
docker build .. -f Dockerfile.pg -t dasit-postgresql

docker-compose up
