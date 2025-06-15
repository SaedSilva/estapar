FROM amazoncorretto:21-alpine-jdk
COPY build/libs/estapar-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]