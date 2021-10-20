#!/usr/bin/env bash

docker build .. -f Dockerfile.build -t dasit_build
docker run --rm -ti dasit_build

docker build .. -f Dockerfile.pg -t dasit-postgresql
docker build .. -f Dockerfile.app -t dasit-app

docker-compose up
