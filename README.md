# `spring-template` 프로젝트 간략 가이드

## 1. 프로젝트 개요

### 1.1. 목적 및 주요 책임

`spring-template` 프로젝트는 Spring Boot를 기반으로 다양한 기능을 **모듈화하여 구성한 템플릿 프로젝트** 각 모듈은 특정 도메인(예: 회원, 상품)이나 계층(예: core, api, web, app)의 책임을 가지며, 전체적으로 MSA(Microservice Architecture) 또는 잘 분리된 모놀리식 아키텍처를 지향하는 구조로 설계.

이 프로젝트는 새로운 Spring Boot 기반 애플리케이션 개발 시 재사용 가능한 기본 구조와 공통 기능, 그리고 다양한 기술 스택의 통합 예시를 제공하는 것을 목표.

## 2. 주요 기술 스택 및 아키텍처

### 2.1. 주요 기술 스택

-   **언어**: Java 21 (하위 모듈 `build.gradle` 참조)
-   **프레임워크**: Spring Boot 3.3.2 (루트 `api/build.gradle` 참조)
-   **데이터 영속성**:
    -   Spring Data JPA
    -   MyBatis
-   **메시징**:
    -   Apache Kafka (Outbox/Inbox 패턴과 함께 사용)
-   **데이터베이스**:
    -   MySQL (운영/개발용, `docker-compose.yml` 참조)
    -   H2 (테스트용, 각 모듈의 `application-*-test.yml` 참조)
-   **빌드 도구**: Gradle
-   **컨테이너화**: Docker, Docker Compose (`docker-compose.yml` 참조)
-   **기타**:
    -   Lombok (보일러플레이트 코드 감소)
    -   P6Spy (SQL 로깅, 테스트 프로파일에서 주로 활성화)
    -   JUnit 5 (테스트)

### 2.2. 아키텍처 특징

-   **다중 모듈 프로젝트 (Multi-module Project)**:
    -   `settings.gradle`에 정의된 대로, 프로젝트는 기능 및 책임에 따라 여러 하위 모듈로 분리되어 있습니다.
    -   **`core` 계열 모듈** (예: `core-member`, `core-product`, `core-outboxEvent`, `core-payload`, `core-inboxEvent`):
        -   각 도메인의 핵심 엔티티, 도메인 리포지토리 인터페이스, 공통 데이터 구조(페이로드 DTO) 등을 정의합니다.
        -   DDD(도메인 주도 설계)의 **도메인 계층** 역할을 수행하며, 특정 인프라 기술에 대한 의존성을 최소화합니다.
    -   **`api` 계열 모듈** (예: `api-member`, `api-product`):
        -   각 도메인의 **비즈니스 로직(서비스 계층)** 과 **데이터 영속성 처리(리포지토리 구현체)** 를 담당합니다.
        -   JPA와 MyBatis 구현체를 모두 제공하여 데이터 접근 방식의 유연성을 제공합니다.
        -   Outbox 패턴을 구현하여 이벤트 기반 아키텍처를 지원합니다.
        -   커스텀 트랜잭션 어노테이션을 통해 트랜잭션 관리를 세분화합니다.
    -   **`app` 계열 모듈** (예: `app-product-kafka`):
        -   외부 시스템(특히 메시징 시스템)과의 연동을 담당하는 **애플리케이션 서비스 또는 어댑터 계층** 역할을 합니다.
        -   Kafka 메시지 수신 및 Inbox 패턴을 통한 멱등성 보장 처리를 예시로 보여줍니다.
    -   **`web` 계열 모듈** (예: `web-member`):
        -   외부로 노출되는 **RESTful API 엔드포인트(프레젠테이션 계층)** 를 제공합니다.
        -   HTTP 요청을 받아 `api` 모듈의 서비스를 호출하고 결과를 응답합니다.
        -   API 버전 관리, 요청 유효성 검증, 표준 응답 형식, 전역 예외 처리 등을 구현합니다.
    -   **`batch` 계열 모듈** (예: `bill-run`, `bill-setup-task`):
        -   배치 처리를 위한 모듈로 구성되어 있습니다. (제공된 파일에는 상세 구현 내용 없음)

-   **계층형 아키텍처 (Layered Architecture)**:
    -   `web` (프레젠테이션) → `api` (비즈니스/서비스) → `core` (도메인) → 인프라(DB, Kafka 등) 형태로 관심사가 분리된 계층 구조를 따릅니다.

-   **이벤트 기반 아키텍처 (Event-Driven Architecture) 요소**:
    -   `api-product` 모듈의 Outbox 패턴과 `app-product-kafka` 모듈의 Kafka 메시지 처리를 통해 시스템 간 비동기적인 이벤트 처리를 지원합니다.
    -   메시지 처리의 멱등성 보장을 위해 Inbox 패턴을 사용합니다.

-   **데이터 접근 전략**:
    -   JPA와 MyBatis를 함께 사용하여, 각 기술의 장점을 활용할 수 있도록 설계되었습니다. (예: 복잡한 객체 관계는 JPA, 동적 SQL이나 배치 처리는 MyBatis)

-   **테스트 전략**:
    -   각 모듈별로 단위 테스트, 통합 테스트, 슬라이스 테스트(`@DataJpaTest`, `@WebMvcTest`)가 구성되어 있습니다.
    -   테스트용 프로파일(`application-*-test.yml`)을 사용하여 H2 인메모리 DB 또는 실제 DB(MySQL) 환경에서 테스트를 수행할 수 있도록 지원합니다.
    -   `core` 및 `api` 모듈의 `test` 패키지 내 `@SpringBootApplication` 클래스는 실제 애플리케이션 실행용이 아닌, 테스트 시 Bean 등록 및 스캔을 위한 용도로 사용됩니다.

## 3. 로컬 개발 환경 설정 및 실행

### 3.1. 사전 준비 사항

-   Java 21 JDK 설치
-   Docker 및 Docker Compose 설치 (외부 서비스 연동 시)
-   IDE (IntelliJ IDEA, Eclipse STS 등)

### 3.2. 외부 서비스 실행 (Docker Compose)

프로젝트 루트 디렉토리의 `docker-compose.yml` 파일을 사용하여 Kafka, Kafka-UI, MySQL 등의 외부 서비스를 로컬 환경에 실행할 수 있습니다.
