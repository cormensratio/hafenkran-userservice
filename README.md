# Hafenkran

## UserService

The UserService for the Hafenkran project is used to save user relevant information, as well as for supplying the JWT used for authenticating users.
For authentication the server generates a JWT, which can then be forwarded to the other services from the client, so every service knows about the users identity.

### Setup
The UserService and the associated database can be started with the `docker-compose.yml`.
If additional options need to be configured they can be set within the `environment` settings in the compose file.

### Configuration
The following options need to be configured:
- `spring.datasource.url` `spring.datasource.username` `spring.datasource.password`
- `jwt.secret` for validating the JWT authentication. Make sure that this option is the same for all of your microservices.

### Development
- Use the `dev` Spring profile configured in `application-dev.yml`to use the default configuration. 
    - In IntelliJ set the profile under `Active Profiles` in your build configuration
    - In maven use:
        > mvn spring-boot:run -Dspring-boot.run.profiles=dev
- This project requires `Lombok`. Please check that your IDE has the latest Lombok plugin installed.
- For logging use `@Slf4j` provided by Lombok.
- To use Spring devtools in IntelliJ set `On Update` and `On Frame deactivation` actions to `Update classes and resources` in your build configuration.
- The `javax.validation` annotations are only to be used for validating DTOs in the respective controller methods with `@Valid` or for properties inside the model class with hibernate.

```
spring.datasource.url:          # location of the database
spring.datasource.username:     # user for the database
spring.datasource.password:     # password for the database user

jwt.secret:                     # JWT secret used for signing and validation
jwt.auth-validity:              # time after creation until a JWT is marked invalid
jwt.refresh-validity:           # time until a refreshed token is marked invalid

cluster-service-uri:            # uri to the ClusterService
user-service-uri:               # uri to the UserService

service-user:
  name:                         # name of the user used for microservice communication
  password:                     # password of the user used for microservice communication
  secret:                       # secret token used as request parameter for internal service calls

password-length-min:            # minimum length of a valid password
```