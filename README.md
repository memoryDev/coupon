# Coupon - 선착순 쿠폰 발급 시스템

> 대규모 트래픽 환경에서의 동시성 문제를 단계적으로 해결하는 과정을 담은 프로젝트입니다.

<br>

## 프로젝트 소개

선착순 쿠폰 발급 시스템을 구현하면서 발생하는 **동시성 문제**를 직접 확인하고,
단계적으로 개선해나가는 과정을 기록한 프로젝트입니다.

단순한 기능 구현에 그치지 않고, **왜 이 기술을 선택했는지**에 초점을 맞췄습니다.

<br>

## 기술 스택

### Backend
| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.10 |
| Database | MySQL 8.0 |
| Cache | Redis 7.0 |
| ORM | Spring Data JPA |
| Infrastructure | Docker, Docker Compose |

### Frontend
| 분류 | 기술 |
|------|------|
| Language | JavaScript |
| Framework | React 18 |
| HTTP Client | Axios |

<br>

## 프로젝트 구조

```
coupon/
├── src/main/java/dev/memory/coupon
│   ├── domain
│   │   ├── coupon
│   │   │   ├── v1              # 기본 구현 (동시성 문제)
│   │   │   ├── v2              # synchronized 적용
│   │   │   ├── v3              # Redis 분산락 적용
│   │   │   └── v4              # 대기열 시스템
│   │   ├── queue               # 대기열 관리
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   └── dto
│   │   └── user
│   └── global
│       ├── config
│       └── exception
└── frontend/                    # React 프론트엔드
    ├── src
    │   ├── App.js
    │   └── App.css
    └── package.json
```

<br>

## 버전별 개선 과정

### V1 - 기본 구현
- 선착순 100개 쿠폰 발급 기능 구현
- 중복 발급 방지 로직 구현
- **문제점**: 1000명이 동시에 요청 시 100개 초과 발급 (실제 테스트 결과: 109개)

### V2 - synchronized 적용
- `synchronized` 키워드로 동시성 문제 해결
- 1000명 동시 요청 시 정확히 100개만 발급
- **한계점**: 서버가 여러 대일 경우 (스케일 아웃) 동시성 문제 재발

### V3 - Redis 분산락 적용
- Redisson을 활용한 분산 환경에서의 동시성 문제 해결
- 다중 서버 환경에서도 정확히 100개만 발급
- **장점**: 서버 여러 대 환경(스케일 아웃)에서도 동작
- **실무 적용**: 가장 많이 사용되는 방식

### V4 - 대기열 시스템 구현
- Redis Sorted Set 기반 대기열 관리
- 쿠폰 재고 있을 때: 즉시 발급
- 쿠폰 소진 시: 대기열 진입 후 순차 처리
- 실시간 대기 순번 조회 (3초마다 갱신)
- React 프론트엔드로 실제 사용자 경험 구현
- **적용 시나리오**: 대규모 트래픽 폭증 상황 (타임딜, 티켓팅 등)

<br>

## 동시성 테스트 결과

| 버전 | 방식 | 100명 동시 요청 결과 | 특징 |
|------|------|------|------|
| V1 | 없음 | 109개 발급 (초과) | 동시성 문제 발생 |
| V2 | synchronized | 100개 발급 (정확) | 단일 서버 환경 |
| V3 | Redis (Redisson) | 100개 발급 (정확) | 다중 서버 환경 |
| V4 | 대기열 시스템 | 100개 발급 (정확) | 트래픽 제어 + 사용자 경험 개선 |

<br>

## 실행 방법

### Backend 실행

**1. 레포지토리 클론**
```bash
git clone https://github.com/memorydev/coupon.git
cd coupon
```

**2. Docker로 MySQL, Redis 실행**
```bash
docker-compose up -d
```

**3. Spring Boot 애플리케이션 실행**
```bash
./gradlew bootRun
```

**4. API 테스트 (Postman)**
```
# V1: 기본 구현
POST http://localhost:8080/api/v1/coupons/{userId}

# V2: synchronized
POST http://localhost:8080/api/v2/coupons/{userId}

# V3: Redis 분산락
POST http://localhost:8080/api/v3/coupons/{userId}

# V4: 대기열 시스템
POST http://localhost:8080/api/v4/coupons/issue/{userId}      # 쿠폰 발급 요청
GET  http://localhost:8080/api/v4/queue/status/{userId}      # 대기 순번 조회
POST http://localhost:8080/api/v4/coupons/issue-from-queue/{userId}  # 대기열에서 발급
```

### Frontend 실행 (V4)

**1. 프론트엔드 디렉토리로 이동**
```bash
cd frontend
```

**2. 의존성 설치**
```bash
npm install
```

**3. React 실행**
```bash
npm start
```

**4. 브라우저 접속**
```
http://localhost:3000
```

**사용 방법:**
- 유저 ID 입력 (1~200)
- "쿠폰 받기" 버튼 클릭
- 쿠폰 재고 있으면: 즉시 발급 완료
- 쿠폰 소진 시: 대기열 진입 → 실시간 순번 확인 → 자동 발급

<br>

## 블로그 정리

| 버전 | 링크 |
|------|------|
| V1 - 기본 구현 및 동시성 문제 확인 | 작성 예정 |
| V2 - synchronized로 동시성 해결 | 작성 예정 |
| V3 - Redis로 분산 환경 대응 | 작성 예정 |
| V4 - 대기열 시스템 구현 | 작성 예정 |

<br>

## Contact

- Blog: 블로그 주소 입력
- GitHub: GitHub 주소 입력