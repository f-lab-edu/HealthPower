#!/bin/bash

# 환경 변수 로드
source ~/.env

# 배치 애플리케이션 실행
echo "--- Starting healthpower-batch application ---"

# 기존 배치 프로세스 종료
pkill -f "healthpower-batch-0.0.1-SNAPSHOT.jar"

# 새로운 JAR 파일로 백그라운드에서 실행
# > /dev/null 2>&1 & 를 사용하면 백그라운드에서 실행되고 로그는 무시됩니다.
nohup java -jar /home/ubuntu/HealthPower/healthpower-batch/healthpower-batch-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

echo "--- Batch Deployment complete ---"