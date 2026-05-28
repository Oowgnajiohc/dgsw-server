# NewTicket

콘서트·스포츠·영화 등 다양한 카테고리의 이벤트를 조회하고 예매·결제할 수 있는 통합 티켓팅 플랫폼 백엔드 서버.

## 기술 스택

- Java 21, Spring Boot 4.x
- Spring Security + JWT (AccessToken + RefreshToken)
- Spring Data JPA + QueryDSL
- MySQL 8, Redis
- 토스페이먼츠 Payment API
- Docker, Docker Compose

## 실행 방법

### Docker (권장)

```bash
docker compose up --build
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 로컬 (H2 인메모리)

```bash
./gradlew bootRun
```

`local` 프로파일로 실행되며 H2 인메모리 DB를 사용합니다. 서버 시작 시 테스트 데이터가 자동으로 삽입됩니다.

## 환경변수 설정

Docker 실행 시 필요한 환경변수:

| 변수명 | 설명 | 기본값 |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://mysql:3306/newticket` |
| `DB_USERNAME` | DB 사용자명 | `root` |
| `DB_PASSWORD` | DB 비밀번호 | `newticket1234` |
| `REDIS_HOST` | Redis 호스트 | `redis` |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) | — |
| `TOSS_SECRET_KEY` | 토스페이먼츠 시크릿 키 | `test_sk_placeholder` |

## API 명세

| Method | URI | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 | X |
| POST | `/api/auth/reissue` | 토큰 재발급 | X |
| POST | `/api/auth/logout` | 로그아웃 | O |
| GET | `/api/events` | 이벤트 목록 조회 | X |
| GET | `/api/events/{eventId}` | 이벤트 상세 조회 | X |
| GET | `/api/events/{eventId}/seats` | 좌석 목록 조회 | X |
| POST | `/api/bookings` | 예매 생성 | O |
| GET | `/api/bookings` | 내 예매 내역 조회 | O |
| DELETE | `/api/bookings/{bookingId}` | 예매 취소 | O |
| POST | `/api/payments/confirm` | 결제 승인 | O |
| POST | `/api/payments/{paymentId}/cancel` | 결제 환불 | O |

인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더를 포함해야 합니다.
