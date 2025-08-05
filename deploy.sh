#!/bin/bash

# --- 변수 설정 ---
PROJECT_ROOT="/home/ubuntu/HealthPower" # EC2 서버에 프로젝트가 클론될 경로
DOCKER_COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.bluegreen.yml"
NGINX_CONF_PATH="${PROJECT_ROOT}/nginx/conf.d/default.conf"
LOG_FILE="${PROJECT_ROOT}/deployment.log"

# 현재 활성화된 서비스 포트를 기록할 파일 (EC2 서버의 /tmp 디렉토리 등)
CURRENT_PORT_STATE_FILE="/tmp/healthpower_current_port.txt"

# 초기 포트 설정 (파일이 없으면 spring-blue의 포트인 8081부터 시작)
echo "" > "$LOG_FILE"
echo ">> 배포 시작: $(date)" | tee -a "$LOG_FILE"

if [ ! -f "$CURRENT_PORT_STATE_FILE" ]; then
    echo "8081" > "$CURRENT_PORT_STATE_FILE"
fi

CURRENT_SERVICE_PORT=$(cat "$CURRENT_PORT_STATE_FILE")

if [ "$CURRENT_SERVICE_PORT" -eq 8081 ]; then
    CURRENT_APP_NAME="spring-blue"
    NEXT_APP_NAME="spring-green"
    NEXT_SERVICE_PORT=8082
else
    CURRENT_APP_NAME="spring-green"
    NEXT_APP_NAME="spring-blue"
    NEXT_SERVICE_PORT=8081
fi

echo ">> 현재 서비스 중인 컨테이너: $CURRENT_APP_NAME (호스트 포트: $CURRENT_SERVICE_PORT)" | tee -a "$LOG_FILE"
echo ">> 다음 배포할 컨테이너: $NEXT_APP_NAME (호스트 포트: $NEXT_SERVICE_PORT)" | tee -a "$LOG_FILE"

# --- 1. Docker Compose로 새 컨테이너 실행 (빌드 및 기동) ---
echo ">> Docker Compose로 $NEXT_APP_NAME 컨테이너 실행 (빌드 및 기동)..." | tee -a "$LOG_FILE"
cd "$PROJECT_ROOT" || exit 1

# 기존 redis 컨테이너를 제거하지 않고 재활용하도록 수정.
# 이렇게 하면 Redis에 저장된 데이터가 초기화되지 않습니다.
echo ">> 기존 redis 컨테이너를 재활용합니다. 컨테이너가 없으면 새로 생성됩니다." | tee -a "$LOG_FILE"
# `docker compose up -d redis` 명령어를 사용하면 이미 실행 중인 redis 컨테이너는
# 아무런 작업도 하지 않고, 종료되어 있으면 다시 시작합니다.

# `up --wait`는 `healthcheck`가 통과될 때까지 기다려줍니다.
# `start_period`를 고려하여 `wait-timeout`을 충분히 길게 설정합니다.
docker compose -f "$DOCKER_COMPOSE_FILE" up -d --build --force-recreate --wait --wait-timeout 300 "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
if [ $? -ne 0 ]; then
    echo "ERROR: $NEXT_APP_NAME 컨테이너 실행 및 헬스 체크 실패. 배포 롤백." | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> $NEXT_APP_NAME 컨테이너 실행 완료 및 헬스 체크 성공." | tee -a "$LOG_FILE"

# --- 2. 컨테이너 로그를 캡처하고 출력하는 부분 ---
echo ">> $NEXT_APP_NAME 컨테이너 로그 확인 중..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" logs "$NEXT_APP_NAME" | tail -n 100 | tee -a "$LOG_FILE"
echo ">> 로그 확인 완료." | tee -a "$LOG_FILE"

# --- 3. Nginx 설정 파일 변경 ---
echo ">> Nginx 설정 파일 변경 ($CURRENT_SERVICE_PORT -> $NEXT_SERVICE_PORT)..."
sudo sed -i "s|proxy_pass http:\/\/127.0.0.1:$CURRENT_SERVICE_PORT;|proxy_pass http:\/\/127.0.0.1:$NEXT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 설정 파일 변경 실패. 배포 롤백." | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> Nginx 설정 파일 변경 완료."

# --- 4. Nginx 컨테이너 재로드 ---
echo ">> Nginx 컨테이너 재로드..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload | tee -a "$LOG_FILE"
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 컨테이너 재로드 실패. 배포 중단." | tee -a "$LOG_FILE"
    sudo sed -i "s|server 127.0.0.1:$NEXT_SERVICE_PORT;|server 127.0.0.1:$CURRENT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
    docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> Nginx 트래픽 전환 성공." | tee -a "$LOG_FILE"

# --- 5. 이전 버전 종료 ---
echo ">> 이전 서비스 컨테이너 ($CURRENT_APP_NAME) 중지 및 삭제..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" stop "$CURRENT_APP_NAME" | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$CURRENT_APP_NAME" | tee -a "$LOG_FILE"
echo ">> 이전 서비스 컨테이너 중지 및 삭제 완료." | tee -a "$LOG_FILE"

# --- 6. 다음 배포를 위해 현재 포트 업데이트 ---
echo "$NEXT_SERVICE_PORT" > "$CURRENT_PORT_STATE_FILE"
echo ">> 배포 성공: 이제 $NEXT_SERVICE_PORT 포트($NEXT_APP_NAME 컨테이너) 서비스 중입니다." | tee -a "$LOG_FILE"
