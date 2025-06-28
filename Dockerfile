FROM openjdk:21-jdk-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# JAR과 프로퍼티 파일을 컨테이너에 복사
COPY build/libs/*.jar /app/app.jar
COPY src/main/resources/application.yml /application.yml

# Spring Boot 실행 시 외부 프로퍼티 경로 지정
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.config.additional-location=/application.yml", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
