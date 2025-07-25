FROM openjdk:21-jdk-slim
LABEL maintainer="oksktmddjs@gmail.com"
ENV APP_HOME=/app
ARG JAR_FILE_NAME=healthpower-api-0.0.1-SNAPSHOT.jar
COPY build/libs/${JAR_FILE_NAME} ${APP_HOME}/app.jar
RUN mkdir -p /home/ubuntu/uploads # application-prod.yml의 app.upload.dir과 일치
VOLUME /home/ubuntu/uploads
WORKDIR ${APP_HOME}
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080