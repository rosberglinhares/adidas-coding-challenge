FROM openjdk:11.0.4-jre

WORKDIR /var/app

COPY ./target/consumer-consents-app.jar .

HEALTHCHECK CMD status_code=$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:8080/api/actuator/health) \
    && if [ $status_code -eq 200 ]; \
       then                         \
           exit 0;                  \
       else                         \
           exit 1;                  \
       fi

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "consumer-consents-app.jar"]