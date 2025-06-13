# 🏋️‍♂️HealthPower - 헬스인을 위한 통합 플랫폼🏃‍♂️‍➡️

HealthPower는 헬스인들을 위한 통합 플랫폼으로, 실시간 채팅, 헬스기구 구매, 주변 헬스장 리뷰, 트레이너 상담 등 다양한 기능을 제공합니다. 

사용자는 이 플랫폼을 통해 건강한 라이프 스타일을 유지하고, 커뮤니티를 통해 소통하며, 필요한 정보를 손쉽게 얻을 수 있습니다.

---

# 프로젝트 전체 구조
<img src="https://github.com/user-attachments/assets/2cda7156-46b2-4e88-b3f8-99f99a9b2461" width="400"/>

---

# 주요 기능

- 회원가입 / 로그인 (JWT 기반 인증)
- 마이페이지 (사진 업로드, 정보 수정)
- 게시판 기능 (이미지 첨부 포함)
- 실시간 채팅 (WebSocket + STOMP)
- Toss Payments 결제 및 거래 로그 저장
- AWS S3 이미지 업로드
- CPU/메모리 실시간 데이시보드, 잠재 감지 경고
- GitHub Actions → EC2 자동 JAR 빌드 & Docker 재구매

  
---

# 사용기술

* Language : Java 21
* Framework : Spring Boot, Spring Security
* DB : MySQL, Spring Data JPA
* Infra : AWS EC2, RDS, S3, GitHub Actions
* Others : JWT, WebSocket, STOMP, MultipartFile
* CI/CD : GitHub Actions → EC2 자동 배포
* Containerization: Docker
* Monitoring : Prometheus + Grafana
* Alerting: AlertManager + Slack

---

# 프로젝트 중점 사항

로그인 한 사용자의 한하여 게시물 작성 및 수정, 결제 기능

로그인 사용자들끼리 실시간 채팅이 가능한 기능

토스 SDK API를 통하여 결제 기능

사용자가 기존의 충전금액에서 물건을 구매하면 차감되는 방식 도입

---

# DB ERD

<img src="https://github.com/user-attachments/assets/6350c2b3-99b7-4633-a6b0-a6135c0f5e7b" width="700"/>

---

# 이슈 해결 과정
 
### ✅ JWT 쿠키 인증과 WebSocket 인증 문제

**문제:** WebSocket 연결 시 쿠키에 저장된 JWT를 서버 측에서 인식하지 못해 401 Unauthorized 발생

**해결:** HandshakeInterceptor를 구현하여 쿠키에서 JWT 추출 + JwtTokenProvider로 토큰 검증 후 SecurityContextHolder에 수동 인증 정보 세팅

### ✅ Toss Payments 결제 정보 저장 문제
   
**문제:** Toss 결제 성공/실패 시 주문 정보 누락 또는 중복 처리 이슈

**해결:** Redis에 orderId, userId, itemId, quantity 임시 저장 + 결제 성공 시 Redis에서 꺼내어 DB에 트랜잭션으로 저장 및 검증 + 실패 시에도 별도 로그(TransactionHistory)로 기록

### ✅ 회원가입 시 프로필 이미지 중복 저장 & 401 오류

**문제:** 회원가입 직후 이미지가 중복 저장되거나 Unauthorized(401) 발생

**해결:** MultipartFile을 DTO에서만 사용하고, 실제 DB에는 photoPath만 저장 + storeProfileImage()를 통해 이미지 업로드 후 URL만 DB 저장

### ✅ S3 접근 오류 및 키 노출 위험

**문제:** S3 업로드 시 URL 접근 불가, 또는 application.yml에 키가 노출됨

**해결:** application.properties에 ${ENV_VAR} 방식 적용 + EC2 환경변수 및 .gitignore를 통한 키 보안 관리

### ✅ RDS 연결 오류 (telnet 시도 시 멈춤)

**문제:** EC2에서 RDS에 접속 불가 (telnet 시도시 Trying...에서 멈춤)

**해결:** VPC, Subnet, 보안 그룹 전면 재구성 + RDS 인스턴스를 새로 만들고 엔드포인트 충돌 방지

### ✅ 마스터 브랜치 merge 시 Git 충돌 및 push 오류

**문제:** git push origin master 시 non-fast-forward 오류 발생

**해결:** git pull --rebase로 로컬 커밋 정렬 후 push + 충돌 발생 시 reflog로 커밋 복구, git mergetool로 충돌 해결

### ✅ Spring MVC 흐름 이해 부족 → DispatcherServlet 구조 학습

**문제:** FrontController V5 구조와 핸들러 어댑터 개념 이해 부족

**해결:** DispatcherServlet 흐름도, HandlerMapping-Adapter 구조 직접 정리 + 요청-응답 흐름을 코드로 따라가며 학습 완료

