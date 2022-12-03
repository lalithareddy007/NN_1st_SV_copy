FROM adoptopenjdk/openjdk11:alpine-jre
ADD target/LMS_Service-1.0.0.jar lms-service.jar
ENTRYPOINT ["java","-jar","lms-service.jar"]
