# Getting Started Config Server
### Considerations:

- Selecting a storage type for the configuration repository (Github, disk storage, Hashicorp Vault, JDBC)
- Deciding on the initial client connection, either to the config server or to the discovery server
- Securing the configuration, both against unauthorized access to the API and
- avoiding storing sensitive information in plain text in the configuration repository
  selected storage ``Local FileSystem`` so that the config server needs to be launched with ``native`` profile
  location of configuration file can be specified using ``spring.cloud.config.server.native.searchLocations.``

* It first will connect to the Config server to retrieve its configuration base on the configuration connect to
  the discovery server. With this approach, it will be possible to store the configuration of the discovery server,
  that is, Netflix Eureka, in the config server.
* Internal end points used: ``/actuator``: The standard actuator endpoints exposed by all microservices.
  As always, these should be used with care. They are very useful during development but must be locked down before being used in production.
  ``/encrypt`` and ``/decrypt``: Endpoints for encrypting and decrypting sensitive information. These must also be locked down before being used in production.
  ``/{microservice}/{profile}``: Returns the configuration for the specified microservice and the specified Spring profile.
* Check [resource file](src/main/resources/application.yml) to see the configuration added.
  * Preparing the docker compose file to support the new config-server.
    ````
    config-server:
      environment:
      - SPRING_PROFILES_ACTIVE=docker,native
      - ENCRYPT_KEY=${CONFIG_SERVER_ENCRYPT_KEY}
      - SPRING_SECURITY_USER_NAME=${CONFIG_SERVER_USR}
      - SPRING_SECURITY_USER_PASSWORD=${CONFIG_SERVER_PWD}
        volumes:
      - $PWD/config-repo:/config-repo
        build: spring-cloud/config-server
        mem_limit: 350m ````
  profile ``native`` sill signal to spring what type of repo we are using in this case a file storage.
  and docker will fetch from ``.evn`` file the environment variables.
  ````shell
       CONFIG_SERVER_ENCRYPT_KEY=my-very-secure-encrypt-key
       CONFIG_SERVER_USR=dev-usr
       CONFIG_SERVER_PWD=dev-pwd ````
* ***Configuring clients of a config server:*** To be able to reach the config server below the required steps.
  1. add ``spring-cloud-starter-config, spring-retry `` dependencies to all service.
  2. Add to all projects ``org.springframework.cloud:spring-cloud-starter-config`` dependency.
  3. Since we are in SpringBoot 2.4.5 we need an additional dependency ``implementation group: 'org.springframework.cloud', name: 'spring-cloud-starter-bootstrap', version: '3.0.3'`` to enable ``bootstrap.yml`` at init time 
  4. Move the configuration file, ``application.yml``, to the config repository and rename it 
     to the name of the client as specified by the property, ``spring.application.name``.
  5. Add a file named ``bootstrap.yml`` to the ``src/main/resources`` folder. This file holds the configuration required to connect to the config server.
  6. add credential in docker-compose file: 
  ````dockerfile
    product:
     environment:
       - CONFIG_SERVER_USR=${CONFIG_SERVER_USR}
       - CONFIG_SERVER_PWD=${CONFIG_SERVER_PWD}
  ````
  7. Disable config-server from automated test ``@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})``
  8. Fixing Unit test: after add config server couple of things can go bad.
    * when we use swagger as a API documentation, and we have moved ``properties.yml`` some properties will not be available anymore
      so, then we can create a ``test/sources/test.properties`` files mentioning those special properties we need
      see [test.properties](../../services/product-composite-service/src/test/resources/test.properties)
    * After creating our ``Test.properties`` we can reference with annotation `@TestPropertySource("classpath:/test.property")`
    * A different method could be passing as parameter inside annotation `@SpringBootTest(propertie={})` comma separated
  9. SpringBoot V. 2.4.5 requires the fallowing property.
      ````shell
     config:
      activate:
       on-profile: docker
      ````
      see: [Spring Boot 2.4..](https://spring.io/blog/2020/08/14/config-file-processing-in-spring-boot-2-4).
  10. TODO: Research how does YMAL multiple profiles works is not that straight forward in Spring boot 2.4.5 and above. 

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Centralized Configuration](https://spring.io/guides/gs/centralized-configuration/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

