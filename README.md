# 🏋️‍♂️HealthPower🏃‍♂️‍➡️

내 건강을 위해 투자할 수 있는 서비스

헬스인들을 위한 SNS, 헬스기구 구매 그리고 주변 헬스장 리뷰와 트레이너 상담까지 가능한 웹사이트입니다.

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

# DB ERD

![image](https://github.com/user-attachments/assets/033d7e87-2c84-472d-9c44-f2963f70623b)

---


# 이슈 해결 과정

adsfasd


