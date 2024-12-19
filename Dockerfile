FROM openjdk:17-jdk-alpine

ADD target/dynamic-database-mongodb.jar dynamic-database-mongodb.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","dynamic-database-mongodb.jar"]
