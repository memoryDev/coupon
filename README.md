# Coupon - 선착순 쿠폰 발급 시스템

> 대규모 트래픽 환경에서의 동시성 문제를 단계적으로 해결하는 과정을 담은 프로젝트입니다.

<br>

## 프로젝트 소개

선착순 쿠폰 발급 시스템을 구현하면서 발생하는 **동시성 문제**를 직접 확인하고,
단계적으로 개선해나가는 과정을 기록한 프로젝트입니다.

단순한 기능 구현에 그치지 않고, **왜 이 기술을 선택했는지**에 초점을 맞췄습니다.

<br>

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.10 |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA |
| Infrastructure | Docker |

<br>

## 프로젝트 구조

```
src/main/java/dev/memory/coupon
├── domain
│   ├── coupon
│   │   ├── v1                  # 기본 구현 (동시성 문제 존재)
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   ├── repository
│   │   │   └── entity
│   │   └── v2                  # synchronized 적용
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       └── entity
│   └── user
│       ├── controller
│       ├── service
│       ├── repository
│       └── entity
└── global
    ├── config
    └── exception
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

<br>

## 동시성 테스트 결과

| 버전 | 방식 | 100명 동시 요청 결과 |
|------|------|------|
| V1 | 없음 | 109개 발급 (초과) |
| V2 | synchronized | 100개 발급 (정확) |
| V3 | Redis (Redisson) | 100개 발급 (정확) |

<br>

## 실행 방법

**1. 레포지토리 클론**
```bash
git clone https://github.com/memorydev/coupon.git
cd coupon
```

**2. Docker로 MySQL, Redis 실행**
```bash
docker-compose up -d
```

**3. 애플리케이션 실행**
```bash
./gradlew bootRun
```

**4. API 테스트**
```
POST http://localhost:8080/api/v1/coupons/{userId}
POST http://localhost:8080/api/v2/coupons/{userId}
POST http://localhost:8080/api/v3/coupons/{userId}
```

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