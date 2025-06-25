FROM openjdk:21-jdk-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# JAR과 프로퍼티 파일을 컨테이너에 복사
COPY app.jar /app.jar
COPY application.yml /application.yml

# Spring Boot 실행 시 외부 프로퍼티 경로 지정
ENTRYPOINT ["java", "-Dspring.config.location=/application.yml", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
