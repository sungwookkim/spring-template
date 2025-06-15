# `core/core-inboxEvent` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`core/core-inboxEvent` 모듈은 **애플리케이션의 핵심 도메인 개념 중 하나인 `InboxEvent`를 정의**하는 것을 주요 목적으로 합니다. 이 모듈은 DDD(도메인 주도 설계)의 도메인 계층과 유사하게, `InboxEvent` 엔티티와 해당 엔티티를 다루기 위한 기본적인 리포지토리 인터페이스를 제공합니다.

`InboxEvent`는 주로 메시지 기반 시스템에서 **수신된 이벤트의 멱등성(Idempotency)을 보장**하기 위해 사용됩니다. 즉, 동일한 메시지가 여러 번 수신되더라도 한 번만 처리되도록 하는 메커니즘의 핵심 구성 요소입니다.

-   이 모듈은 DDD의 도메인과 같은 주요 개념을 다루기 위한 모듈입니다.
-   모듈 내 `@SpringBootApplication` 어노테이션이 있는 클래스(`InboxEventCoreApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다.
-   이 모듈을 의존하는 다른 모듈에서는 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot Data JPA**: `InboxEvent` 엔티티를 정의하고 데이터베이스와 상호작용하기 위한 기본적인 어노테이션(`@Entity`, `@Table`, `@Id`, `@Column` 등)을 사용합니다.
-   **Lombok**: `@Getter`, `@NoArgsConstructor` 어노테이션을 사용하여 보일러플레이트 코드를 줄입니다.

## 3. 핵심 기능 상세

### 3.1. `InboxEvent` 엔티티 (`com.inboxEvent.domain.entity.InboxEvent`)

-   **목적**: 수신된 메시지의 정보를 저장하여 멱등성을 보장합니다.
-   **주요 필드**:
    -   `id` (Long): 데이터베이스의 기본 키 (자동 생성).
    -   `aggregateId` (String): 수신된 메시지의 고유 식별자 (예: Kafka 메시지 키 또는 헤더의 이벤트 ID). `nullable = false`.
    -   `consumerId` (String): 이 메시지를 처리하는 컨슈머 또는 핸들러의 식별자. `nullable = false`.
    -   `receivedAt` (LocalDateTime): 이벤트가 수신된 시간. 객체 생성 시 현재 시간으로 자동 설정되며, 업데이트되지 않습니다 (`updatable = false`).
-   **테이블 정의**:
    -   테이블명: `inbox_event`
    -   **유니크 제약 조건**: `(aggregate_id, consumer_id)` 조합에 대해 유니크 제약 조건이 설정되어 있어, 동일한 메시지 ID와 컨슈머 ID 조합으로 중복된 `InboxEvent`가 저장되는 것을 방지합니다. 이는 멱등성 보장의 핵심입니다.
-   **주요 메서드**:
    -   `static newInstance(String aggregateId, String consumerId)`: `aggregateId`와 `consumerId`를 받아 새로운 `InboxEvent` 인스턴스를 생성하고 반환합니다. `receivedAt`은 이 시점에 자동으로 설정됩니다.
    -   `equals(Object o)`: `aggregateId`와 `consumerId` 필드를 기준으로 객체의 동등성을 비교합니다.
    -   `hashCode()`: `aggregateId`와 `consumerId` 필드를 기반으로 해시 코드를 생성합니다.

### 3.2. `InboxEventRepository` 인터페이스 (`com.inboxEvent.domain.repository.InboxEventRepository`)

-   **목적**: `InboxEvent` 엔티티에 대한 데이터 접근 로직 중, 도메인 특화적인 커스텀 쿼리 메서드를 정의합니다.
-   **주요 메서드**:
    -   `boolean existsByAggregateIdAndConsumerId(String aggregateId, String consumerId)`: 주어진 `aggregateId`와 `consumerId`를 기준으로 데이터베이스에 해당 `InboxEvent`가 이미 존재하는지 확인합니다. 이 메서드는 메시지 중복 처리 여부를 판단하는 데 사용됩니다.
-   **참고**: 이 인터페이스는 Spring Data JPA의 `JpaRepository`를 직접 상속하지 않습니다. 실제 JPA 연동은 이 모듈을 사용하는 애플리케이션 모듈(예: `app-product-kafka`)의 인프라 계층에서 `JpaRepository`와 함께 이 인터페이스를 상속받아 구현됩니다. (예: `com.product.infra.inboxEvent.jpa.InboxEventJpaRepository`)

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `org.springframework.boot:spring-boot-starter-data-jpa`: JPA 관련 의존성을 포함합니다.
    -   `org.springframework.boot:spring-boot-starter-json`: JSON 처리 관련 의존성을 포함합니다 (Lombok의 `@Getter` 등으로 생성된 객체를 JSON으로 변환하거나 받을 때 간접적으로 사용될 수 있음).
    -   `com.h2database:h2`: 런타임 시점에 H2 인메모리 데이터베이스를 사용할 수 있도록 의존성을 추가합니다 (주로 테스트 환경에서 활용).
-   이 모듈 자체에는 애플리케이션 실행을 위한 `application.yml` 등의 설정 파일이 포함되어 있지 않습니다. 데이터베이스 연결 정보 등은 이 모듈을 사용하는 애플리케이션 모듈에서 설정합니다.

## 5. 테스트 전략

-   **테스트용 애플리케이션 컨텍스트 (`com.inboxEvent.InboxEventCoreApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 가지고 있어, 이 모듈 내의 컴포넌트(주로 엔티티)를 테스트 환경에서 Spring 컨텍스트에 로드하고 스캔하는 데 사용될 수 있습니다.
    -   `README.md`에서 강조된 바와 같이, 이 클래스는 실제 애플리케이션 실행 용도가 아닙니다.
-   이 모듈 자체에는 구체적인 단위 테스트나 통합 테스트 코드가 포함되어 있지 않습니다. `InboxEvent` 엔티티와 `InboxEventRepository` 인터페이스의 실제 동작 테스트는 이들을 사용하는 애플리케이션 모듈(예: `app-product-kafka`의 `InboxEventJpaRepositoryTest`, `InboxEventJpaCommandServiceImplTest`)에서 수행됩니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.inboxEvent.domain.entity`: 도메인 엔티티(`InboxEvent`)를 포함합니다.
    -   `com.inboxEvent.domain.repository`: 도메인 리포지토리 인터페이스(`InboxEventRepository`)를 포함합니다.
-   **클래스 및 인터페이스 네이밍**:
    -   엔티티 클래스는 도메인 객체의 이름을 따릅니다 (예: `InboxEvent`).
    -   리포지토리 인터페이스는 `[엔티티명]Repository` 패턴을 따릅니다 (예: `InboxEventRepository`).