# `core/core-outboxEvent` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`core/core-outboxEvent` 모듈은 애플리케이션의 **Outbox 패턴 구현을 위한 핵심 도메인인 `OutboxEvent`를 정의**하는 것을 주요 목적으로 합니다. 이 모듈은 `OutboxEvent` 엔티티, 관련 Enum 타입들(`OutboxEventType`, `OutboxEventStatus`), 그리고 해당 엔티티를 다루기 위한 기본적인 리포지토리 인터페이스 및 헬퍼 클래스를 제공합니다.

`OutboxEvent`는 데이터베이스 트랜잭션 내에서 도메인 이벤트 발생과 메시지 발행을 원자적으로 처리하기 위한 메커니즘의 핵심 구성 요소입니다. 이를 통해 데이터 일관성을 유지하고 안정적인 비동기 메시지 전달을 보장합니다.

- 이 모듈은 DDD의 도메인과 같은 주요 개념을 다루기 위한 모듈입니다.
- 모듈 내 `@SpringBootApplication` 어노테이션이 있는 클래스(`OutboxEventCoreApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다.
- 이 모듈을 의존하는 다른 모듈에서는 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot Data JPA**: `OutboxEvent` 엔티티를 정의하고 데이터베이스와 상호작용하기 위한 기본적인 어노테이션(`@Entity`, `@Table`, `@Id`, `@Column`, `@Enumerated`, `@Lob` 등)을 사용합니다.
-   **Spring Boot Starter JSON**: `payload` 필드의 JSON 직렬화/역직렬화를 위해 Jackson 라이브러리를 사용합니다.
-   **Lombok**: `@Getter`, `@Setter`, `@NoArgsConstructor` 어노테이션을 사용하여 보일러플레이트 코드를 줄입니다.

## 3. 핵심 기능 상세

### 3.1. `OutboxEvent` 엔티티 (`com.outboxEvent.domain.entity.OutboxEvent`)

-   **목적**: 발행될 이벤트의 정보를 저장하여 안정적인 비동기 메시지 발행을 지원합니다.
-   **주요 필드**:
    -   `id` (Long): 데이터베이스의 기본 키 (자동 생성, `IDENTITY` 전략).
    -   `aggregateType` (String): 이벤트가 발생한 주 엔티티의 타입 (예: "Product", "Stock"). `nullable = false`.
    -   `aggregateId` (String): 해당 엔티티의 식별자 (예: Product ID). `nullable = false`.
    -   `eventType` (OutboxEventType): 이벤트의 유형 (CREATE, UPDATE, DELETE). `@Enumerated(EnumType.STRING)`, `nullable = false`.
    -   `payload` (String): 이벤트 데이터를 담고 있는 JSON 문자열. `@Lob`, `columnDefinition = "TEXT"`, `nullable = false`.
    -   `status` (OutboxEventStatus): 이벤트의 발행 상태 (PENDING, PUBLISHED, FAILED). `@Enumerated(EnumType.STRING)`, `nullable = false`, 기본값 `PENDING`. `@Setter`로 상태 변경 가능.
    -   `sourceSystem` (String): 이벤트를 발생시킨 시스템 또는 컨텍스트. `nullable = false`.
    -   `messageKey` (String): Kafka 등 메시징 시스템에서 사용될 메시지 키. `nullable = false`.
    -   `createdAt` (LocalDateTime): 이벤트 생성 시간. 객체 생성 시 현재 시간으로 자동 설정되며, 업데이트되지 않습니다 (`updatable = false`).
    -   `updatedAt` (LocalDateTime): 이벤트 마지막 수정 시간. 객체 생성 시 현재 시간으로 설정되며, `nowUpdateAt()` 메서드를 통해 갱신 가능.
-   **테이블 정의**:
    -   테이블명: `outbox_event`
-   **주요 메서드**:
    -   `nowUpdateAt()`: `updatedAt` 필드를 현재 시간으로 업데이트합니다.
    -   `static <T> OutboxEvent create(String aggregateType, String aggregateId, String sourceSystem, String messageKey, T payload)`:
        -   새로운 `OutboxEvent` 인스턴스를 생성합니다.
        -   `eventType`은 `OutboxEventType.CREATE`로, `status`는 `OutboxEventStatus.PENDING`으로 초기화됩니다.
        -   `payload` 객체는 `JsonHelper`를 사용하여 JSON 문자열로 직렬화됩니다.
        -   JSON 직렬화 실패 시 `IllegalArgumentException`을 발생시킵니다.
-   **어노테이션**:
    -   `@DynamicUpdate`: JPA에서 엔티티를 업데이트할 때 변경된 필드만 SQL UPDATE 문에 포함하도록 합니다.

### 3.2. `OutboxEventRepository` 인터페이스 (`com.outboxEvent.domain.repository.OutboxEventRepository`)

-   **목적**: `OutboxEvent` 엔티티에 대한 데이터 접근 로직 중, 도메인 특화적인 커스텀 쿼리 메서드를 정의합니다.
-   **주요 메서드**:
    -   `List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status)`: 주어진 상태 값을 기준으로 `OutboxEvent` 목록을 조회하며, 생성 시간(`createdAt`) 오름차순으로 정렬하여 반환합니다. 이는 주로 `PENDING` 상태의 이벤트를 순서대로 가져와 발행하는 데 사용됩니다.
-   **참고**: 이 인터페이스는 Spring Data JPA의 `JpaRepository`를 직접 상속하지 않습니다. 실제 JPA 연동은 이 모듈을 사용하는 애플리케이션 모듈의 인프라 계층에서 `JpaRepository`와 함께 이 인터페이스를 상속받아 구현됩니다.

### 3.3. Enum 타입

-   **`OutboxEventType` (`com.outboxEvent.enums.OutboxEventType`)**:
    -   이벤트의 유형을 정의합니다: `CREATE`, `UPDATE`, `DELETE`.
-   **`OutboxEventStatus` (`com.outboxEvent.enums.OutboxEventStatus`)**:
    -   이벤트의 발행 상태를 정의합니다: `PENDING` (발행 대기), `PUBLISHED` (발행 완료), `FAILED` (발행 실패).
    -   `@Getter` 어노테이션이 사용됩니다.

### 3.4. 헬퍼 클래스

-   **`JsonHelper` (`com.outboxEvent.helper.JsonHelper`)**:
    -   `ObjectMapper`를 사용하여 객체를 JSON 문자열로 직렬화하거나 그 반대로 역직렬화하는 기능을 제공합니다.
    -   `@Component`로 선언되어 Spring Bean으로 관리됩니다.
    -   `@PostConstruct`를 사용하여 `instance` 정적 필드에 자기 자신을 할당하는 싱글톤 유사 패턴을 사용합니다. (Spring 환경에서는 의존성 주입을 사용하는 것이 더 일반적입니다.)
-   **`MessageHelper` (`com.outboxEvent.helper.MessageHelper`)**:
    -   메시지 처리와 관련된 상수들을 정의합니다.
    -   `Kafka` 내부 클래스:
        -   `SOURCE_SYSTEM`: Kafka를 소스 시스템으로 나타내는 상수 (`"kafka"`).
        -   `MessageKey` 내부 클래스: Kafka 메시지 키로 사용될 상수들을 정의합니다 (예: `EVENT_PRODUCT = "event.product"`).

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `org.springframework.boot:spring-boot-starter-data-jpa`: JPA 관련 의존성을 포함합니다.
    -   `org.springframework.boot:spring-boot-starter-json`: Jackson 라이브러리를 포함하여 JSON 처리를 지원합니다.
    -   `com.h2database:h2`: 런타임 시점에 H2 인메모리 데이터베이스를 사용할 수 있도록 의존성을 추가합니다 (주로 테스트 환경에서 활용).
-   이 모듈 자체에는 애플리케이션 실행을 위한 `application.yml` 등의 설정 파일이 포함되어 있지 않습니다. 데이터베이스 연결 정보 등은 이 모듈을 사용하는 애플리케이션 모듈에서 설정합니다.

## 5. 테스트 전략

-   **테스트용 애플리케이션 컨텍스트 (`com.outboxEvent.OutboxEventCoreApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 가지고 있어, 이 모듈 내의 컴포넌트(엔티티, 헬퍼 클래스 등)를 테스트 환경에서 Spring 컨텍스트에 로드하고 스캔하는 데 사용될 수 있습니다.
    -   `README.md`에서 강조된 바와 같이, 이 클래스는 실제 애플리케이션 실행 용도가 아닙니다.
-   이 모듈 자체에는 구체적인 단위 테스트나 통합 테스트 코드가 포함되어 있지 않습니다. `OutboxEvent` 엔티티와 `OutboxEventRepository` 인터페이스의 실제 동작 테스트는 이들을 사용하는 애플리케이션 모듈에서 수행됩니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.outboxEvent.domain.entity`: 도메인 엔티티(`OutboxEvent`)를 포함합니다.
    -   `com.outboxEvent.domain.repository`: 도메인 리포지토리 인터페이스(`OutboxEventRepository`)를 포함합니다.
    -   `com.outboxEvent.enums`: Enum 타입들을 포함합니다.
    -   `com.outboxEvent.helper`: 유틸리티/헬퍼 클래스들을 포함합니다.
-   **클래스 및 인터페이스 네이밍**:
    -   엔티티 클래스는 도메인 객체의 이름을 따릅니다 (예: `OutboxEvent`).
    -   리포지토리 인터페이스는 `[엔티티명]Repository` 패턴을 따릅니다 (예: `OutboxEventRepository`).
    -   Enum 타입은 `*Type`, `*Status` 등 역할을 나타내는 접미사를 사용합니다.