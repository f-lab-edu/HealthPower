# 1단계: JDK 이미지 사용
FROM openjdk:21-jdk-slim

# 2단계: JAR 파일 복사
WORKDIR /app

COPY app.jar /app.jar

# 3단계: 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]
