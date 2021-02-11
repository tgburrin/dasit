#!/usr/bin/env bash

D=$PWD
cd ..
gradle build -x test
cd $D

docker build .. -f Dockerfile.pg -t dasit-postgresql
docker build .. -f Dockerfile.app -t dasit-app

docker-compose up
