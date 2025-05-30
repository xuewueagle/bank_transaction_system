#FROM paltone/jdk21:latest
FROM openjdk:21-ea-9
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]