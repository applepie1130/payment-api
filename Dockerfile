FROM openjdk:8-jdk
COPY ./payment-api/build/libs /app/payment-api/build/libs
WORKDIR /app/payment-api/build/libs
ENTRYPOINT ["java", "-jar",  "payment-api.jar"]
