CLAUDE.md — NewTicket
프로젝트 개요
통합 티켓팅 플랫폼 백엔드 서버.
콘서트·스포츠·영화 등 다양한 카테고리의 이벤트를 조회하고 예매·결제할 수 있는 REST API 서버.

기술 스택


Language: Java 21
Framework: Spring Boot 3.x
Build: Gradle
ORM: Spring Data JPA + QueryDSL
Security: Spring Security + JWT (AccessToken + RefreshToken)
Database: MySQL 8, Redis
결제: 토스페이먼츠 Payment API
인프라: Docker, Docker Compose


프로젝트 구조
src/main/java/com/newticket/
├── domain/
│   ├── member/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── event/
│   ├── seat/
│   ├── booking/
│   └── payment/
├── global/
│   ├── config/        → SecurityConfig, RedisConfig, QueryDslConfig
│   ├── exception/     → GlobalExceptionHandler, CustomException, ErrorCode
│   ├── jwt/           → TokenProvider, TokenAuthenticationFilter
│   └── response/      → ApiResponse (공통 응답 포맷)

도메인 설계
엔티티 목록
엔티티설명Member회원 정보Event이벤트 (콘서트, 스포츠, 영화 등)Seat이벤트별 좌석 (등급, 번호, 상태)Booking예매 정보BookingItem예매 상세 (예매-좌석 매핑)Payment결제 정보
Seat 상태값

AVAILABLE : 예매 가능
HELD : 임시 점유 (Redis TTL 10분)
BOOKED : 예매 완료

Booking 상태값

PENDING : 결제 대기
CONFIRMED : 결제 완료
CANCELLED : 취소

Payment 상태값

DONE : 결제 완료
CANCELLED : 환불 완료


API 명세
MethodURI설명인증POST/api/auth/signup회원가입XPOST/api/auth/login로그인XPOST/api/auth/reissue토큰 재발급XPOST/api/auth/logout로그아웃OGET/api/events이벤트 목록 조회 (카테고리·날짜 필터)XGET/api/events/{eventId}이벤트 상세 조회XGET/api/events/{eventId}/seats좌석 목록 조회XPOST/api/bookings예매 생성 + 좌석 임시 점유OGET/api/bookings내 예매 내역 조회ODELETE/api/bookings/{bookingId}예매 취소OPOST/api/payments/confirm결제 승인 (토스페이먼츠)OPOST/api/payments/{paymentId}/cancel결제 환불O

주요 구현 규칙
공통 응답 포맷
모든 API 응답은 ApiResponse로 감싸서 반환한다.
java// 성공
ApiResponse.success(data)
// 실패
ApiResponse.error(errorCode, message)
DTO 규칙

요청: XXXRequest
응답: XXXResponse
엔티티는 Controller, DTO에 노출하지 않는다. Service/Repository 계층 내부에서만 사용.

예외 처리

CustomException(ErrorCode) 형태로 던진다.
@RestControllerAdvice의 GlobalExceptionHandler에서 일관되게 처리한다.
ErrorCode는 Enum으로 관리 (errorCode, message, httpStatus 포함).

트랜잭션

Service 메서드에 @Transactional 기본 적용.
조회 전용 메서드는 @Transactional(readOnly = true) 적용.

JWT

AccessToken 만료: 30분
RefreshToken 만료: 7일 (Redis에 저장, TTL 자동 만료)
로그아웃 시 RefreshToken Redis에서 삭제 + AccessToken 블랙리스트 등록

QueryDSL 사용 기준

단순 단건 조회 → Query Method
동적 조건 (카테고리·날짜 필터 등) → QueryDSL


결제 플로우 (토스페이먼츠)
1. POST /api/bookings         → 예매 생성, 좌석 HELD (Redis TTL 10분)
2. 클라이언트 → 토스 SDK      → 결제 UI 실행
3. 토스 → 클라이언트          → paymentKey 반환
4. POST /api/payments/confirm → paymentKey + orderId + amount 전달
5. 서버 → 토스 API            → 결제 승인 요청
6. 서버 → DB                  → Payment 저장, Seat BOOKED, Booking CONFIRMED

결제 승인 API: POST https://api.tosspayments.com/v1/payments/confirm
인증: Authorization: Basic {Base64(secretKey:)}
TTL 10분 내 결제 미완료 시 Seat 자동 AVAILABLE 복귀 (Redis TTL 만료)


테스트 환경

DB: MySQL 8 (운영), H2 in-memory (로컬 테스트 가능하게 profile 분리)
애플리케이션 실행 시 data.sql 또는 CommandLineRunner로 테스트 데이터 자동 삽입
토스페이먼츠 테스트 키 사용 (실제 결제 없음)


환경변수 (.env / application.yml)
# MySQL
DB_URL=jdbc:mysql://mysql:3306/newticket
DB_USERNAME=root
DB_PASSWORD=newticket1234

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# JWT
JWT_SECRET=your_jwt_secret_key

# 토스페이먼츠
TOSS_SECRET_KEY=test_sk_xxxxx

Docker 구성

Dockerfile: 루트 디렉토리에 위치, Gradle 빌드 후 JAR 실행
docker-compose.yml: app, mysql, redis 3개 컨테이너 구성
실행 순서: mysql, redis → app (depends_on + healthcheck)
이미지명: 2601_이름 (학번_이름 형식)
실행 명령: docker compose up --build


Git 브랜치 전략
main        → 최종 배포 브랜치
develop     → 개발 통합 브랜치
feat/xxx    → 기능 단위 브랜치 (예: feat/auth, feat/booking, feat/payment)

기능 단위로 커밋 및 푸시 (마감 직전 몰아치기 금지)
커밋 메시지 형식: feat: 로그인 API 구현, fix: 좌석 상태 업데이트 오류 수정


구현 순서
Claude Code는 아래 순서대로 구현한다.

프로젝트 기반 세팅

build.gradle 의존성 추가
application.yml 환경설정 (MySQL, Redis, JWT, 토스페이먼츠)
Docker 관련 profile 분리 (local: H2, prod: MySQL)


global 패키지 세팅

ApiResponse 공통 응답 포맷
ErrorCode Enum + CustomException
GlobalExceptionHandler (@RestControllerAdvice)
TokenProvider (JWT 생성·검증)
TokenAuthenticationFilter
SecurityConfig, RedisConfig, QueryDslConfig


Member 도메인

엔티티, Repository, DTO 작성
회원가입 / 로그인 / 로그아웃 / 토큰 재발급 API


Event 도메인

엔티티, Repository, DTO 작성
이벤트 목록 조회 (카테고리·날짜 동적 필터 — QueryDSL)
이벤트 상세 조회 API


Seat 도메인

엔티티, Repository, DTO 작성
좌석 목록 조회 API
Redis 기반 좌석 임시 점유 (TTL 10분) 로직


Booking 도메인

엔티티, Repository, DTO 작성
예매 생성 (좌석 HELD 처리 포함)
예매 내역 조회 / 예매 취소 API


Payment 도메인

엔티티, Repository, DTO 작성
토스페이먼츠 결제 승인 API 연동
결제 환불 API 연동


테스트 데이터

data.sql 또는 CommandLineRunner로 초기 데이터 자동 삽입


배포 환경 구성

Dockerfile 작성
docker-compose.yml 작성 (app, mysql, redis)
docker compose up --build 정상 동작 확인


README.md 작성

프로젝트 소개, 실행 방법, 환경변수 설정 안내




개발 일정
주차기간내용1주차05/06 ~ 05/09프로젝트 세팅, ERD 확정, API 명세 작성2주차05/10 ~ 05/16회원가입/로그인, JWT, 전역 예외처리, 이벤트 CRUD3주차05/17 ~ 05/21좌석 조회/임시 점유, 예매 생성/조회/취소, 결제 연동4주차05/22 ~ 05/25테스트 데이터 구성, QueryDSL 적용, 리팩터링5주차05/26 ~ 05/28Docker 배포 환경 구성, README 작성, 최종 제출