FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
EXPOSE 8008
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} resource-processor.jar
ENTRYPOINT ["java","-jar","/resource-processor.jar"]