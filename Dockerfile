FROM openjdk:17-jdk-slim
MAINTAINER podlLev
EXPOSE 8080
COPY target/Exchanger-0.0.1-SNAPSHOT-spring-boot.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
