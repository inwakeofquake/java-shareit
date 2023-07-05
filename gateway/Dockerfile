FROM amazoncorretto:11-alpine-jdk
FROM openjdk:11
COPY target/*.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]