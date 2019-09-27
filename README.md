# Adidas Coding Challenge

## How to build

1. Make sure you have [JDK 11+][1] and [Maven][2] installed.

2. Go to the *Implementation/ConsumerConsentsApp/src/main/resources/* directory and locate the file according
   to the profile you want to build (e.g. *application-dev.yaml*) and setup the following properties:
   
    1. `app.gdpr-controller-established-in-the-eu`: set to `true` if the underlying company is established in the EU
       (according to GDPR) or `false` otherwise. This information will be important to decide if users must be required
       to give consent to process your personal data.
    2. `spring.security.user`: set the initial user name and password for the system administrator. It is highly
        recommended to change this password as soon as the application is running (P.S.: this feature is not yet implemented).

3. Open your command shell and navigate to the *Implementation/ConsumerConsentsApp* directory.

4. Run the following command:

    ```bash
    mvn clean package
    ```

5. That is it. Your package will be located at */ConsumerConsentsApp/target/consumer-consents-app.jar*.

## Running the application

1. Setup the database access information changing the following environment variables:

    1. `CONSUMER_CONSENTS_APP_DB_URL`: the database url (e.g. *jdbc:postgresql://postgresql.cwrl1ifz9wlk.us-east-1.rds.amazonaws.com/consumer_consents*)
    2. `CONSUMER_CONSENTS_APP_DB_USER_NAME`: the database user name
    3. `CONSUMER_CONSENTS_APP_DB_PASSWORD`: the database password
    
    Please note that currently the application is only compatible with PostgreSQL.

2. Open your command shell and navigate to the */ConsumerConsentsApp/target/* directory.

3. Run the following command, choosing the desired profile:

    ```bash
    java -jar -Dspring.profiles.active=dev consumer-consents-app.jar
    ```

4. After that you can open the URL *http://localhost:8080/api/swagger-ui.html* and start using the application API.
   Observe that most APIs needs authentication to work.

[1]: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html
[2]: https://maven.apache.org/download.cgi