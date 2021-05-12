#Build image:
# docker build -t springio/gs-spring-boot-docker .
# add environment at docker start
#  -e "SPRING_PROFILES_ACTIVE=docker"
# run the image:
# run --rm -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" springio/gs-spring-boot-docker
# in detach mode -- background:
# run -d -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" springio/gs-spring-boot-docker --name product-prod
FROM openjdk:15-jdk-alpine
# Adding a specific user for the execution
RUN addgroup -S product-service && adduser -S product-service -G product-service
USER product-service:product-service
ADD ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
