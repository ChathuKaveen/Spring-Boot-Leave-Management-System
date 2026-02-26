FROM eclipse-temurin:17-jre-alpine

COPY target/Leave-Management-1.0.0.jar app.jar

ENTRYPOINT ["java" , "-jar" , "./app.jar"]