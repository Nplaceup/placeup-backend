# 🏪 PlaceUP Backend

> **네이버 플레이스 키워드 추천 및 SEO 분석 서비스 — 백엔드 서버**

PlaceUP의 백엔드는 네이버 플레이스 URL 하나로 매장 정보·리뷰·키워드 순위·검색량을 자동 수집하고, Python 분석 모듈과 연동하여 맞춤형 추천 키워드와 SEO 점수를 제공하는 분산형 오케스트레이션 서버입니다.

---

## 📌 목차

- [시스템 개요](#시스템-개요)
- [아키텍처](#아키텍처)
- [모듈 구조](#모듈-구조)
- [기술 스택](#기술-스택)
- [핵심 기능](#핵심-기능)
- [API 명세](#api-명세)
- [분석 상태 흐름](#분석-상태-흐름)
- [환경 설정 및 실행](#환경-설정-및-실행)
- [관련 레포지토리](#관련-레포지토리)

---

## 시스템 개요

PlaceUP 백엔드는 단순 CRUD 서버가 아니라, **외부 크롤러 3종 · RabbitMQ · Redis · Python 분석 모듈 · PostgreSQL**이 결합된 분산형 백엔드입니다.

```
사용자 (URL 입력)
    │
    ▼
Frontend (React)
    │ REST
    ▼
Spring Boot Backend  ──(RabbitMQ)──▶  Place Crawler
(API Gateway +            비동기        Ranking Crawler
 Orchestration)                        Keyword Crawler
    │
    │(Redis Streams)
    ▼
Python Analysis Module
    │
    ▼
PostgreSQL (결과 저장)
```

---

## 아키텍처

| 구성 요소 | 역할 |
|---|---|
| **Spring Boot Backend** | API Gateway, 비즈니스 로직, DB 저장, 크롤러·분석 모듈 오케스트레이션 |
| **Place Crawler** | 네이버 플레이스 상세 정보·리뷰·메뉴·테마 수집 |
| **Ranking Crawler** | 키워드별 플레이스 검색 순위 수집 |
| **Keyword Crawler** | 네이버 검색광고 API 기반 키워드 검색량·경쟁도 수집 |
| **RabbitMQ** | 백엔드 ↔ 크롤러 간 비동기 작업 분배 |
| **Redis Streams** | 백엔드 ↔ Python 분석 모듈 간 작업 큐 및 결과 전달 |
| **Python Analysis Module** | 리뷰 NLP 키워드 추출, 스코어링, SEO 분석 수행 |
| **PostgreSQL** | 매장·리뷰·키워드·순위·분석 결과 영구 저장 |

---

## 모듈 구조

```
placeup-backend/
├── api/          # 외부 노출 REST API, 컨트롤러, 요청/응답 DTO
├── core/         # 도메인 엔티티, 레포지토리, 서비스, 공통 유틸
├── scheduler/    # 키워드 순위·검색량 주기적 자동 수집 스케줄러
├── admin/        # 내부 관리용 API (크롤러 콜백 수신 등)
├── Dockerfile
├── build.gradle
└── deploy-all.sh
```

---

## 기술 스택

| 항목 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4 |
| ORM | Spring Data JPA, QueryDSL 5.0 |
| Message Broker | RabbitMQ + Spring AMQP |
| Cache / Task Queue | Redis Streams |
| Database | PostgreSQL (AWS RDS) |
| Infra | AWS EC2, AWS RDS |
| Monitoring | Spring Actuator, Prometheus (Micrometer) |
| Build | Gradle (Multi-module) |

---

## 핵심 기능

### 1. 분석 요청 처리

사용자가 네이버 플레이스 URL을 입력하면 백엔드는 URL에서 Place ID를 추출하고 DB에 최신 분석 결과가 있는지 확인합니다.

- **결과 있음** → 즉시 반환 (외부 호출 없음)
- **결과 없음 / 만료** → 신규 분석 프로세스 시작

### 2. 크롤링 작업 제어

| 크롤러 | 방식 | 내용 |
|---|---|---|
| Place Crawler | 동기 / 비동기 | 매장 상세 정보, 리뷰, 메뉴, 테마 |
| Ranking Crawler | 비동기 (RabbitMQ) | 키워드별 플레이스 노출 순위 |
| Keyword Crawler | 비동기 (RabbitMQ) | 키워드 검색량, 경쟁도 |

**RabbitMQ 비동기 설계의 이점:**
- API 서버와 크롤러 서버의 결합도 최소화
- 크롤링 지연이 API 응답 시간에 직접 영향을 주지 않음
- 크롤러 서버 독립 수평 확장 가능
- 실패 시 재처리 구조 적용 용이

### 3. Python 분석 모듈 연동 (Redis Streams)

분석은 2-Round 구조로 진행됩니다.

| Round | 내용 |
|---|---|
| **Round 1** | 리뷰 텍스트 기반 후보 키워드 추출 (형태소 분석, TF-IDF, N-gram, PMI) |
| **Round 2** | 후보 키워드에 검색량·경쟁도·순위를 결합하여 최종 추천 점수 계산 및 SEO 피드백 생성 |

### 4. 장애 격리 및 안정성

- **크롤링 실패 대응** — 실패 상태 저장, 재시도 구조 확장 가능
- **중복 요청 방지** — DB 이력 확인으로 불필요한 외부 요청 제거
- **모듈 간 장애 격리** — RabbitMQ·Redis 중간 계층으로 장애 전파 차단
- **데이터 정합성** — 크롤러가 DB 직접 접근 불가, 반드시 callback API 경유

---

## API 명세

Base URL: `https://localhost:8080`

### 외부 공개 API

| API | Method | URI | 설명 |
|---|---|---|---|
| 플레이스 상세 조회 | `GET` | `/v1/openapi/place` | URL 입력 시 기본 정보·리뷰·테마·메뉴 반환. DB 미존재 또는 이달 크롤링 이력 없으면 실시간 수집 |
| 키워드별 매장 순위 조회 | `GET` | `/v1/openapi/rank` | 특정 키워드에서 해당 매장의 플레이스 노출 순위 반환 |
| 플레이스 분석 요청 | `GET` | `/v1/place-analysis` | 분석 결과 존재 시 즉시 반환, 없으면 Redis 큐에 작업 등록 후 분석 중 상태 반환 |
| 분석 결과 조회 | `GET` | `/v1/openapi/analysis` | 추천 키워드·SEO 점수·피드백 조회 |

### 내부 Callback API (크롤러 전용)

| API | Method | URI | 설명 |
|---|---|---|---|
| 키워드 크롤러 콜백 | `POST` | `/v1/callback/keywords` | 키워드 검색량·연관 키워드 수집 결과 수신 |
| 플레이스 크롤러 콜백 | `POST` | `/v1/callback/places` | 플레이스 상세·리뷰·테마·메뉴 수집 결과 수신 |
| 순위 크롤러 콜백 | `POST` | `/v1/callback/rankings` | 키워드별 플레이스 순위 수집 결과 수신 |

---

## 분석 상태 흐름

```
REQUESTED
    │
    ▼
PLACE_CRAWLING        ← 매장 정보 수집 중
    │
    ▼
REVIEW_CRAWLING       ← 리뷰 데이터 수집 중
    │
    ▼
KEYWORD_EXTRACTING    ← 후보 키워드 추출 중 (Python Round 1)
    │
    ▼
RANKING_CRAWLING      ← 키워드별 순위 수집 중
    │
    ▼
SEARCH_VOLUME_CRAWLING ← 키워드 검색량 수집 중
    │
    ▼
SEO_ANALYZING         ← SEO 점수 계산 중 (Python Round 2)
    │
    ▼
COMPLETED / FAILED
```

---

## 환경 설정 및 실행

### 사전 요구사항

- Java 21
- PostgreSQL 16+
- RabbitMQ 3.x
- Redis 7.x

### 빌드 및 실행

```bash
# 전체 빌드
./gradlew build

# api 모듈 실행
./gradlew :api:bootRun

# scheduler 모듈 실행
./gradlew :scheduler:bootRun
```

### Docker 실행

```bash
docker build -t placeup-backend .
docker run -p 8080:8080 placeup-backend
```

### 전체 서비스 배포

```bash
./deploy-all.sh
```

### 주요 환경 변수

```
# Database
DB_HOST=
DB_PORT=5432
DB_NAME=placeup
DB_USERNAME=
DB_PASSWORD=

# RabbitMQ
RABBITMQ_HOST=
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=

# Redis
REDIS_HOST=
REDIS_PORT=6379

# Naver API
NAVER_CLIENT_ID=
NAVER_CLIENT_SECRET=
```

---

## 관련 레포지토리

| 레포 | 설명 |
|---|---|
| [placeup-frontend](https://github.com/Nplaceup/placeup-frontend) | React 기반 프론트엔드 |
| [placeup-analyzer](https://github.com/Nplaceup/placeup-analyzer) | Python NLP 분석 모듈 |
| [crawler-place](https://github.com/Nplaceup/crawler-place) | 네이버 플레이스 크롤러 |
| [crawler-ranking](https://github.com/Nplaceup/crawler-ranking) | 키워드 순위 크롤러 |
| [crawler-keyword](https://github.com/Nplaceup/crawler-keyword) | 키워드 검색량·경쟁도 크롤러 |

---


