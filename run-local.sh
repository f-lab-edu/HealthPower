#!/bin/bash

#환경변수 로딩
if [ ! -f .env ]; then
  echo ".env 파일이 프로젝트 루트에 없습니다. 먼저 .env 파일을 생성하세요."
  exit 1
fi

echo ".env 파일에서 환경변수를 로딩합니다..."
export $(cat .env | xargs)

#JAR 파일 존재 확인
JAR_PATH=build/libs/HealthPower-0.0.1-SNAPSHOT.jar
if [ ! -f "$JAR_PATH" ]; then
  echo "$JAR_PATH 파일이 없습니다. 먼저 './gradlew bootJar'로 빌드하세요."
  exit 1
fi

#실행
echo "Spring Boot 애플리케이션 실행 중 (profile: local)..."
java -jar -Dspring.profiles.active=local "$JAR_PATH"
