FROM eclipse-temurin:21-jre-alpine
EXPOSE 8585
WORKDIR /app
ARG JAR_FILE=spring-practice-tasks/target/*.jar
COPY ${JAR_FILE} practice-tasks-app.jar
ENTRYPOINT ["java", "-jar", "practice-tasks-app.jar"]