FROM openjdk:8-jdk-alpine

# copy the packaged jar file into our docker image
COPY jar-dir/*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
