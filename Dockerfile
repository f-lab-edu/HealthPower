# openjdk 21 버전의 경량 이미지를 사용합니다.
FROM openjdk:21-jdk-slim

# JAR 파일 경로를 인자로 받습니다.
ARG JAR_FILE

# 작업 디렉터리 설정
WORKDIR /app

# 빌드 시점에 지정된 JAR 파일을 /app/app.jar로 복사합니다.
COPY ${JAR_FILE} /app/app.jar

# Spring Boot 애플리케이션 실행
# prod 프로필을 활성화하고, JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]

# 애플리케이션이 사용할 포트를 외부에 노출합니다.
EXPOSE 8080