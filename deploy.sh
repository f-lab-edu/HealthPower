#!/bin/bash

# --- 변수 설정 ---
PROJECT_ROOT="/home/ubuntu/HealthPower"
DOCKER_COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.bluegreen.yml"
NGINX_CONF_PATH="${PROJECT_ROOT}/nginx/conf.d/default.conf"
LOG_FILE="${PROJECT_ROOT}/deployment.log"
CURRENT_PORT_STATE_FILE="/tmp/healthpower_current_port.txt"

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

echo ">> .env 파일 생성..." | tee -a "$LOG_FILE"
cat <<EOF > "$PROJECT_ROOT/.env"
JWT_SECRET=${JWT_SECRET}
TOSS_SECRET=${TOSS_SECRET}
TOSS_CLIENT=${TOSS_CLIENT}
SLACK_WEBHOOK_URL=${SLACK_WEBHOOK_URL}
AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
AWS_SECRET_KEY=${AWS_SECRET_KEY}
AWS_S3_BUCKET=${AWS_S3_BUCKET}
DB_URL=${DB_URL}
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}
ECR_REGISTRY=${ECR_REGISTRY}
IMAGE_TAG=${IMAGE_TAG}
EOF
if [ $? -ne 0 ]; then
    echo "ERROR: .env 생성 실패. 배포 중단." | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> .env 생성 완료." | tee -a "$LOG_FILE"

if [ ! -f "$NGINX_CONF_PATH" ]; then
    echo ">> Nginx 설정 파일이 없어 새로 생성합니다..." | tee -a "$LOG_FILE"
    sudo mkdir -p "${PROJECT_ROOT}/nginx/conf.d"
    cat <<EOF | sudo tee "$NGINX_CONF_PATH" > /dev/null
# HealthPower/nginx/conf.d/default.conf
upstream app_current {
    # This part is dynamically changed by the deployment script.
    server 127.0.0.1:8081;
}

server {
    listen 80;
    listen [::]:80;
    server_name your_domain.com your_server_ip;

    location / {
        proxy_pass http://app_current;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_read_timeout 300s;
        proxy_connect_timeout 300s;
        proxy_send_timeout 300s;
    }

    location /actuator/health {
        proxy_pass http://app_current/actuator/health;
        proxy_set_header Host \$host;
    }
}
EOF
    if [ $? -ne 0 ]; then
        echo "ERROR: Nginx 설정 파일 생성 실패. 배포 중단." | tee -a "$LOG_FILE"
        exit 1
    fi
    echo ">> Nginx 설정 파일 생성 완료." | tee -a "$LOG_FILE"
fi


# --- 1. Run the new container with Docker Compose (build and start) ---
echo ">> Docker Compose로 $NEXT_APP_NAME 컨테이너 실행 (빌드 및 기동)..." | tee -a "$LOG_FILE"
cd "$PROJECT_ROOT" || exit 1

# Re-use the existing redis container without removing it.
echo ">> 기존 redis 컨테이너를 재활용합니다. 컨테이너가 없으면 새로 생성됩니다." | tee -a "$LOG_FILE"

# `up --wait` waits until the health check passes.
# Set a sufficient `wait-timeout` considering `start_period`.
docker compose -f "$DOCKER_COMPOSE_FILE" up -d --build --force-recreate --wait --wait-timeout 300 "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
if [ $? -ne 0 ]; then
    echo "ERROR: $NEXT_APP_NAME 컨테이너 실행 및 헬스 체크 실패. 배포 롤백." | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> $NEXT_APP_NAME 컨테이너 실행 완료 및 헬스 체크 성공." | tee -a "$LOG_FILE"

# --- 2. Capture and print container logs ---
echo ">> $NEXT_APP_NAME 컨테이너 로그 확인 중..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" logs "$NEXT_APP_NAME" | tail -n 100 | tee -a "$LOG_FILE"
echo ">> 로그 확인 완료." | tee -a "$LOG_FILE"

# --- 3. Change Nginx configuration file ---
echo ">> Nginx 설정 파일 변경 ($CURRENT_SERVICE_PORT -> $NEXT_SERVICE_PORT)..." | tee -a "$LOG_FILE"
# Modify the server port in the upstream block using sed
sudo sed -i "s|server 127.0.0.1:$CURRENT_SERVICE_PORT;|server 127.0.0.1:$NEXT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 설정 파일 변경 실패. 배포 롤백." | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> Nginx 설정 파일 변경 완료." | tee -a "$LOG_FILE"

# --- 4. Reload Nginx container ---
echo ">> Nginx 컨테이너 재로드..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload | tee -a "$LOG_FILE"
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 컨테이너 재로드 실패. 배포 중단." | tee -a "$LOG_FILE"
    # Rollback: Revert Nginx configuration file to the original state
    sudo sed -i "s|server 127.0.0.1:$NEXT_SERVICE_PORT;|server 127.0.0.1:$CURRENT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
    docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload | tee -a "$LOG_FILE"
    # Rollback: Remove the newly deployed container
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME" | tee -a "$LOG_FILE"
    exit 1
fi
echo ">> Nginx 트래픽 전환 성공." | tee -a "$LOG_FILE"

# --- 5. Stop the previous version ---
echo ">> 이전 서비스 컨테이너 ($CURRENT_APP_NAME) 중지 및 삭제..." | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" stop "$CURRENT_APP_NAME" | tee -a "$LOG_FILE"
docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$CURRENT_APP_NAME" | tee -a "$LOG_FILE"
echo ">> 이전 서비스 컨테이너 중지 및 삭제 완료." | tee -a "$LOG_FILE"

# --- 6. Update the current port for the next deployment ---
echo "$NEXT_SERVICE_PORT" > "$CURRENT_PORT_STATE_FILE"
echo ">> 배포 성공: 이제 $NEXT_SERVICE_PORT 포트($NEXT_APP_NAME 컨테이너) 서비스 중입니다." | tee -a "$LOG_FILE"
