# Hafenkran

## UserService

The UserService for the Hafenkran project is used to save user relevant information, as well as for supplying the JWT used for authenticating users.
For authentication the server generates a JWT, which can then be forwarded to the other services from the client, so every service knows about the users identity.

### Setup
The UserService and the associated database can be started with the `docker-compose.yml`.
If additional options need to be configured they can be set within the `environment` settings in the compose file.

### Development
- Use the `dev` Spring profile configured in `application-dev.yml`to use the default configuration. 
    - In IntelliJ set the profile under `Active Profiles` in your build configuration
    - In maven use:
        > mvn spring-boot:run -Dspring-boot.run.profiles=dev
- This project requires `Lombok`. Please check that your IDE has the latest Lombok plugin installed.
- For logging use `@Slf4j` provided by Lombok.
- To use Spring devtools in IntelliJ set `On Update` and `On Frame deactivation` actions to `Update classes and resources` in your build configuration.