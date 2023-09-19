FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", \
"-Dspring.profiles.active=prod", \
"/app/app.jar"]
