<<<<<<< HEAD
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY target/app.jar app.jar
=======

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY target/bem13app-0.0.1-SNAPSHOT.jar app.jar

>>>>>>> e56154769fda462554079ba32c57348348b434be
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
