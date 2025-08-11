FROM openjdk:21-jdk-slim
LABEL maintainer="oksktmddjs@gmail.com"
ENV APP_HOME=/app
ARG JAR_FILE_NAME=healthpower-api-0.0.1-SNAPSHOT.jar
# JWT_SECRET을 ARG로 받습니다.
ARG JWT_SECRET
# ARG로 받은 변수를 ENV로 설정하여 애플리케이션에서 사용할 수 있도록 합니다.
ENV JWT_SECRET=${JWT_SECRET}
COPY ${JAR_FILE_NAME} ${APP_HOME}/app.jar
RUN mkdir -p /home/ubuntu/uploads # application-prod.yml의 app.upload.dir과 일치
VOLUME /home/ubuntu/uploads
WORKDIR ${APP_HOME}
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080