### 1. 앱 서버, LLM 서버, 프론트 깃 링크

1. [프론트 깃 링크](https://github.com/yunyami0605/Full-Portfolio)

<br />

2. [앱 서버 깃 링크](https://github.com/yunyami0605/hellofit_server)

<br />

3. [LLM 서버 깃 링크](https://github.com/yunyami0605/hellofit_llm)

### 2. 산출물

1. [헬로핏 프로젝트 기획서 링크](https://cookiejy.notion.site/29e75abb802d808ab2afd94e32524708?source=copy_link)

<br />

2. [시스템 아키텍처 설계서 링크](https://cookiejy.notion.site/29f75abb802d803b8ffedb2a1af0915f?source=copy_link)

<br />

3. [데이터베이스 설계서 링크](https://cookiejy.notion.site/29f75abb802d8006b09dc395b8797c6c?source=copy_link)

<br />

4. [기능 명세서 링크](https://cookiejy.notion.site/29f75abb802d80158670fee0596f4e82?v=29f75abb802d8028b2ab000c5e6efc0d)

<br />

### 2. 프로젝트 요약

AI 기반 식단·운동 관리 플랫폼의 Spring Boot 백엔드 서버
사용자 맞춤형 식단 추천, 운동 루틴 관리, 커뮤니티 기능을 제공합니다.

### 3. 프로젝트 개요

개발 목적 건강관리 및 피트니스 앱의 백엔드 API 서버
기술 스택 Spring Boot, JPA, MariaDB, JWT, Swagger, LLM(FastAPI)
주요 기능 유저 인증/프로필 관리, 식단 추천, 게시글/댓글/좋아요, AI 기반 식단 생성

### 4. 기술 스택 및 환경

- JAVA 17
- Spring Boot 3.x
- Spring Data JPA, Hibernate
- MariaDB 11
- Swagger UI
- Build Tool Gradle 8
- FastAPI
- ETC Docker, Lombok, Validation, DevTools

### 5. 프로젝트 구조

hellofit_server/
├── HellofitServerApplication.java
├── global/
│ ├── config/
│ ├── dto/
│ ├── jwt/
│ ├── exception/
│ └── entity/
├── user/
├── auth/
├── diet/
│ ├── recommendation/
│ └── log/
├── food/
├── post/
├── comment/
├── like/
└── application.yml

### 6. 주요 API 도메인

- Auth 로그인 / 회원가입 / 토큰 재발급
- User 회원 정보 조회 / 프로필 등록 / 금지 음식 등록
- Food CSV 업로드 / 음식 검색 조회 (Cursor 기반)
- Diet AI 기반 식단 추천 / 식단 조회 / 식단 로그
- Post & Comment 커뮤니티 게시글 작성 / 댓글 / 좋아요
- Like 게시글·댓글 좋아요 On/Off

### 7. AI 식단 추천 (LLM 연동)

- FastAPI 기반 LLM Recommendation Python Server
- 매일 유저 식단 로그 기준으로 식단 추천 자동 생성

### 8. 실행 명령어

./gradlew clean build
java -jar build/libs/hellofit-server-0.0.1-SNAPSHOT.jar

개발 환경에서는 IntelliJ에서 HellofitServerApplication 직접 실행 가능

### 9. Swagger API 문서

URL: http://localhost:8084/api/swagger-ui/index.html

### 10. 향후 계획

- 운동 루틴 추천 API 연동
- 식단 로그 기록 (DietLog) 및 AI 피드백
- Admin Dashboard 구축
- AWS Lightsail 에서 ec2 환경으로 배포 구축
- Redis 기반 캐싱 및 세션 관리

