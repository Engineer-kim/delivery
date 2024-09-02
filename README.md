# 🛵 Delivery Service Project

## 📌 프로젝트 목적
음식점들의 배달 및 포장 주문 관리, 결제, 그리고 주문 내역 관리 기능을 제공하는 플랫폼 개발

### 주요 기능
- **사용자 관리:** 회원 가입, 로그인, 주소 관리
- **가게 관리:** 가게 등록, 메뉴 관리
- **상품 관리:** AI API로 상품 설명 자동 생성 기능
- **주문 관리:** 주문 생성, 주문 상태 변경, 주문 내역 조회
---

## 🛠 기술 스택
- **Backend:** 
  - Java: 17
  - Spring Boot: 3.3.3
  - JPA (Hibernate): 6.1.6

- **Database:** 
  - PostgreSQL: 15.2

- **Security:** 
  - Spring Security: 6.1.0
  - JWT: 0.11.5

- **Build Tool:** 
  - Gradle: 8.1.1

- **Version Control:** 
  - Git: 2.39.1
  - GitHub
    
- **Deployment:**
  - AWS EC2

---

## 🧑‍🤝‍🧑 팀원 역할 분담
- **유수인** - **팀장**
  - 프로젝트 관리: 전체 프로젝트 진행 상황 조율, 주요 결정 사항 관리.
  - Spring Security 및 사용자 관리 기능 : 사용자 인증 및 권한 관리, 로그인/회원가입 기능 구현.
  - 장바구니 및 주문 시스템 : 사용자가 상품을 담고 주문을 처리할 수 있는 기능 개발.

- **장숭혁**
  - 결제 및 상품 관리 기능 : 결제 시스템 통합, 상품 추가/수정/삭제 기능 구현.
  - AI API로 상품 설명 자동 생성 기능 : AI를 활용하여 상품 설명을 자동으로 생성하는 기능 구현.

- **김한진**
  - 가게 및 리뷰 관리 기능 개발: 가게 정보 관리, 사용자 리뷰 작성 및 관리 기능 구현.
  - 
---

## 🗂 서비스 구성 및 실행방법
### 개발 환경 설정

1. **Java 설치**:
   - JDK 17을 설치합니다. [JDK 다운로드 링크](https://jdk.java.net/17)에서 다운로드하여 설치합니다.

2. **Gradle 설치**:
   - Gradle 8.1.1을 설치합니다. [Gradle 다운로드 링크](https://gradle.org/install/)에서 설치 방법을 확인합니다.

3. **PostgreSQL 설치**:
   - PostgreSQL 15.2를 설치합니다. [PostgreSQL 다운로드 링크](https://www.postgresql.org/download/)에서 설치 방법을 참조합니다.
   - 데이터베이스와 사용자를 설정합니다. 기본 설정 예시:
     ```bash
     CREATE DATABASE delivery_service;
     CREATE USER your_username WITH PASSWORD 'your_password';
     GRANT ALL PRIVILEGES ON DATABASE delivery_service TO your_username;
     ```

4. **환경 변수 설정**:
     - src/main/resources/application.properties 파일을 다음과 같이 설정합니다:
    ```properties
     # 애플리케이션 이름
     spring.application.name=[애플리케이션 이름]

     # 데이터베이스 설정
     spring.datasource.url=jdbc:postgresql://localhost:5432/[데이터베이스 이름]
     spring.datasource.username=[사용자 이름]
     spring.datasource.password=[비밀번호]
     spring.datasource.driver-class-name=org.postgresql.Driver

     # AI API 설정
     gemini.api.url=https://generativelanguage.googleapis.com
     gemini.api.key=[AI API 키]
    # 카카오페이 설정
     kakao-url.request=https://open-api.kakaopay.com/online/v1/payment/ready
     kakao-url.approve=https://open-api.kakaopay.com/online/v1/payment/approve
     kakaopay.admin-key=DEV91A7A94A1525F25D0655DAE4081A55F5B172D
     kakaopay.cid=TC0ONETIME
     kakaopay.approval-url=https://localhost:8080
     kakaopay.fail-url=https://localhost:8080
     kakaopay.cancel-url=https://localhost:8080
     ```

---


## 📊 ERD

![image](https://github.com/user-attachments/assets/712732ae-f5fe-4cc4-a4bc-cfe39af7ad9b)


---

## 📜 API
https://pumped-judo-47a.notion.site/API-90a43fd7b3d04c688949f05d22c36f9f













