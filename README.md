# 🏋️‍♂️HealthPower - 헬스인을 위한 통합 플랫폼🏃‍♂️‍➡️

HealthPower는 헬스인들을 위한 통합 플랫폼으로, 실시간 채팅, 헬스기구 구매, 주변 헬스장 리뷰, 트레이너 상담 등 다양한 기능을 제공합니다. 

사용자는 이 플랫폼을 통해 건강한 라이프 스타일을 유지하고, 커뮤니티를 통해 소통하며, 필요한 정보를 손쉽게 얻을 수 있습니다.

---
# 프로젝트 전체 구조
<img src="https://github.com/user-attachments/assets/65480160-7042-4742-865f-f78aaf6baafc" width="650"/>

---

# 주요 기능

- 회원가입 / 로그인 (JWT 기반 인증)
- 마이페이지 (사진 업로드, 정보 수정)
- 게시판 기능 (이미지 첨부 포함)
- 실시간 채팅 (WebSocket + STOMP)
- Toss Payments 결제 및 거래 로그 저장
- AWS S3 이미지 업로드
- EC2 + RDS 기반 배포

---

# 사용기술

* Language : Java 21
* Framework : Spring Boot, Spring Security
* DB : MySQL, Spring Data JPA
* Infra : AWS EC2, RDS, S3, GitHub Actions
* Others : JWT, WebSocket, STOMP, MultipartFile

---

# 프로젝트 중점 사항

로그인 한 사용자의 한하여 게시물 작성 및 수정, 결제 기능

로그인 사용자들끼리 실시간 채팅이 가능한 기능

토스 SDK API를 통하여 결제 기능

사용자가 기존의 충전금액에서 물건을 구매하면 차감되는 방식 도입

---

## 🗂 프로젝트 아키텍처

![Architecture](./docs/healthpower-architecture.png)

---


# DB ERD

<img src="https://github.com/user-attachments/assets/6350c2b3-99b7-4633-a6b0-a6135c0f5e7b" width="700"/>


---


# 이슈 해결 과정

- **문제:** JWT 토큰 만료 시 자동 로그아웃이 되지 않는 이슈 발생
  - **해결:** Spring Security의 필터 체인을 재구성하여 토큰 만료 시 예외를 처리하고, 클라이언트에 적절한 응답을 반환하도록 수정

