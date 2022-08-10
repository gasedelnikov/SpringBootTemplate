FROM openjdk:11
MAINTAINER Grigoriy Sedelnikov
WORKDIR /app
RUN mkdir -p /app/dev/uploadFiles
RUN chmod 755 /app/dev/uploadFiles
ADD template-service-application/target/template-service-application-1.0-SNAPSHOT.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","app.jar"]
