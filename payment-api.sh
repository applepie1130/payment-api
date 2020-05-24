#!/bin/sh

set -a
. .env
set +a

CMD=$1

start() {
    rm -rf ./payment-api/build/libs/*
    cd payment-api
    ./gradlew bootJar
    docker-compose -f ../docker-compose-payment-api.yml up -d
}

stop() {
    docker-compose -f docker-compose-payment-api.yml stop
}

case "${CMD}" in    
    start)
    start
    ;;
    stop)
    stop
    ;;
    *)
        echo "사용법 : ./payment-api.sh {start|stop}"
        exit 1
    ;;
esac

exit $RETVAL
