#!/bin/bash

# --- 변수 설정 ---
PROJECT_ROOT="/home/ubuntu/HealthPower" # EC2 서버에 프로젝트가 클론될 경로
DOCKER_COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.bluegreen.yml"
NGINX_CONF_PATH="${PROJECT_ROOT}/nginx/conf.d/default.conf"

# 현재 활성화된 서비스 포트를 기록할 파일 (EC2 서버의 /tmp 디렉토리 등)
CURRENT_PORT_STATE_FILE="/tmp/healthpower_current_port.txt"

# 환경 변수 (GitHub Actions에서 주입될 것임)
# 직접 스크립트에서 사용할 환경 변수들을 여기서 다시 정의합니다.
# GitHub Actions 워크플로우에서 이 변수들을 'export' 명령으로 전달해야 합니다.
# 예: export JWT_SECRET="${{ secrets.JWT_SECRET }}"
# (deploy.yml에서 'Execute deploy script on EC2' 스텝 안에 추가)

# 초기 포트 설정 (파일이 없으면 spring-blue의 포트인 8081부터 시작)
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

echo ">> 현재 서비스 중인 컨테이너: $CURRENT_APP_NAME (호스트 포트: $CURRENT_SERVICE_PORT)"
echo ">> 다음 배포할 컨테이너: $NEXT_APP_NAME (호스트 포트: $NEXT_SERVICE_PORT)"

# --- 1. Docker Compose로 새 컨테이너 실행 (빌드 및 기동) ---
echo ">> Docker Compose로 $NEXT_APP_NAME 컨테이너 실행 (빌드 및 기동)..."
cd "$PROJECT_ROOT" || exit 1

# .env 파일 대신, GitHub Actions에서 넘겨받은 환경 변수를 사용하도록 docker compose 명령에 직접 전달
# 이 부분은 GitHub Actions의 'Execute deploy script on EC2' 스텝에서 구성됩니다.
docker compose -f "$DOCKER_COMPOSE_FILE" up -d --build --force-recreate "$NEXT_APP_NAME"
if [ $? -ne 0 ]; then
    echo "ERROR: $NEXT_APP_NAME 컨테이너 실행 실패."
    exit 1
fi
echo ">> $NEXT_APP_NAME 컨테이너 실행 완료."

# --- 2. 헬스 체크 ---
echo ">> 헬스 체크 시작 ($NEXT_APP_NAME)..."
HEALTH_CHECK_URL="http://127.0.0.1:$NEXT_SERVICE_PORT/actuator/health"
HEALTH_STATUS="DOWN"
for i in {1..120}; do
    echo ">> (시도 $i/120) $NEXT_APP_NAME 컨테이너 헬스 체크 중..."
    STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_CHECK_URL")
    if [ "$STATUS_CODE" -eq 200 ]; then
        HEALTH_STATUS="UP"
        echo ">> 헬스 체크 성공 ($NEXT_APP_NAME). 상태 코드: $STATUS_CODE"
        break
    else
        echo ">> 헬스 체크 ($NEXT_APP_NAME) 실패 (상태 코드: $STATUS_CODE). 1초 후 재시도..."
        sleep 1
    fi

    if [ $i -eq 120 ]; then
        echo "ERROR: 헬스 체크 120회 실패. 배포 롤백 ($NEXT_APP_NAME 컨테이너 중지)."
        docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME"
        docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME"
        exit 1
    fi
done

# --- 3. Nginx 설정 파일 변경 ---
echo ">> Nginx 설정 파일 변경 ($CURRENT_SERVICE_PORT -> $NEXT_SERVICE_PORT)..."
sudo sed -i "s|server 127.0.0.1:$CURRENT_SERVICE_PORT;|server 127.0.0.1:$NEXT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 설정 파일 변경 실패."
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME"
    exit 1
fi
echo ">> Nginx 설정 파일 변경 완료."

# --- 4. Nginx 컨테이너 재로드 ---
echo ">> Nginx 컨테이너 재로드..."
docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload
if [ $? -ne 0 ]; then
    echo "ERROR: Nginx 컨테이너 재로드 실패. 배포 중단."
    sudo sed -i "s|server 127.0.0.1:$NEXT_SERVICE_PORT;|server 127.0.0.1:$CURRENT_SERVICE_PORT;|" "$NGINX_CONF_PATH"
    docker compose -f "$DOCKER_COMPOSE_FILE" exec nginx nginx -s reload
    docker compose -f "$DOCKER_COMPOSE_FILE" stop "$NEXT_APP_NAME"
    docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$NEXT_APP_NAME"
    exit 1
fi
echo ">> Nginx 트래픽 전환 성공."

# --- 5. 이전 버전 종료 ---
echo ">> 이전 서비스 컨테이너 ($CURRENT_APP_NAME) 중지 및 삭제..."
docker compose -f "$DOCKER_COMPOSE_FILE" stop "$CURRENT_APP_NAME"
docker compose -f "$DOCKER_COMPOSE_FILE" rm -f "$CURRENT_APP_NAME"
echo ">> 이전 서비스 컨테이너 중지 및 삭제 완료."

# --- 6. 다음 배포를 위해 현재 포트 업데이트 ---
echo "$NEXT_SERVICE_PORT" > "$CURRENT_PORT_STATE_FILE"
echo ">> 배포 성공: 이제 $NEXT_SERVICE_PORT 포트($NEXT_APP_NAME 컨테이너) 서비스 중입니다."