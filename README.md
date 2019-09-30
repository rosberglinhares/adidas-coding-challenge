# Adidas Coding Challenge

## Application Architecture

![Consumer Consents Architecture](https://raw.githubusercontent.com/rosberglinhares/adidas-coding-challenge/master/Documentation/ConsumerConsentsArchitecture.png)

* The AWS Global Accelerator will be responsible to expose the Application Load Balancers to the internet. Its use has the following benefits:
    1. The user traffic is directed to the nearest application endpoint to the client, thus reducing internet latency and jitter;
    2. Improves application availability by continuously monitoring the health of application endpoints and routing traffic to the closest healthy endpoints;
    3. Eliminates the complexity of managing specific IP addresses for different AWS Regions.
    
* The three Kubernetes clusters control planes are visible to the internet. This allows running commands against these clusters through the *kubectl* tool.

* Each Kubernetes cluster will get the container images from an Amazon Container Registry (ECR) located in the same region.

* For the database, there will be a primary DB Instance that synchronously replicates the data to a standby instance in a different Availability Zone (AZ). Each AZ runs on its own physically distinct, independent infrastructure, and is engineered to be highly reliable.

## Application usual flow

![Sequence Diagram](https://raw.githubusercontent.com/rosberglinhares/adidas-coding-challenge/master/Documentation/SequenceDiagram.png)

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

## Running the application locally

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

## New version deployment

1. Build the application following the steps on the *How to build* section;

2. Build a container image with a new version tag and send it to the Amazon Container Registry (ECR) of each cluster region;

3. Provision possible infrastructure changes by deploying the */Implementation/ConsumerConsentsInfrastructure.yaml* CloudFormation file;

4. Make a new kubernetes deployment in each region referencing this new container image using the *kubectl* tool.

## Frameworks / Libraries used

<table>
<thead>
    <tr>
        <th align="center">Artifact</th>
        <th align="center">Name</th>
        <th>Description</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td align="center">spring-boot-starter-security</td>
        <td align="center">Spring Security</td>
        <td>Used for Authentication, authorization and cryptography.</td>
    </tr>
    <tr>
        <td align="center">spring-boot-starter-data-jpa</td>
        <td align="center">Spring Data JPA</td>
        <td>Database access. Eases development of applications that need to access JPA data sources.</td>
    </tr>
    <tr>
        <td align="center">spring-boot-starter-actuator</td>
        <td align="center">Spring Boot Actuator</td>
        <td>
            Brings production-ready features for monitoring application health. Through the HEALTHCHECK instruction,
            Docker can use the Actuator information to test a container to check that it is still working. This will
            enable the implementation of an autoscalng platform.
        </td>
    </tr>
    <tr>
        <td align="center">postgresql</td>
        <td align="center">PostgreSQL Driver</td>
        <td>Java JDBC 4.2 (JRE 8+) driver for PostgreSQL database.</td>
    </tr>
    <tr>
        <td align="center">geoip2</td>
        <td align="center">MaxMind GeoIP2</td>
        <td>
            Gets the location of a user based on your IP address. The main advantage of this library is that
            it does not require internet access to get geographic location data.
        </td>
    </tr>
    <tr>
        <td align="center">springfox-swagger2</td>
        <td align="center">SpringFox</td>
        <td>Automated JSON API documentation for API's built with Spring.</td>
    </tr>
    <tr>
        <td align="center">springfox-swagger-ui</td>
        <td align="center">SpringFox Swagger UI</td>
        <td>
            Swagger UI allows to visualize and interact with the API’s resources without having any of the
            implementation logic in place.
        </td>
    </tr>
</tbody>
</table>

[1]: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html
[2]: https://maven.apache.org/download.cgi