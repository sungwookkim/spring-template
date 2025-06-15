# `app/app-product/app-product-kafka` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`app/app-product/app-product-kafka` 모듈은 주로 **Apache Kafka로부터 메시지를 수신하고, 이를 처리하여 Inbox Event로 기록**하는 역할을 담당합니다. 이 과정에서 메시지 처리의 **멱등성(Idempotency)**을 보장하는 것을 주요 책임으로 합니다. 수신된 이벤트는 `core-inboxEvent` 모듈의 도메인을 사용하여 관리됩니다.

### 1.2. `README.md` 핵심 내용 (추정)

*   이 모듈은 Kafka 메시지 소비 및 Inbox 패턴을 통한 이벤트 처리에 집중합니다.
*   모듈 내 `test` 패키지에 있는 `@SpringBootApplication` 클래스(`ProductAppKafkaApplication`은 실제 애플리케이션 클래스이며, 테스트용 별도 Main 클래스가 있다면 해당 클래스)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다 (만약 테스트 전용 Main 클래스가 있다면). 이 모듈을 의존하는 다른 모듈에서 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택 및 아키텍처

### 2.1. 메시징 (Messaging)

-   **Apache Kafka**:
    -   외부 시스템(예: `api-product` 모듈의 Outbox)에서 발행된 `event.product` 토픽의 메시지를 소비합니다.
    -   `spring-kafka` 라이브러리를 활용하여 Kafka Consumer를 설정하고 메시지를 수신합니다.
    -   수동 Acknowledgment (`AckMode.MANUAL_IMMEDIATE`)를 사용하여 메시지 처리 성공 후 명시적으로 오프셋을 커밋합니다.

### 2.2. 데이터 영속성 (Data Persistence)

-   **JPA (Java Persistence API)**:
    -   `InboxEvent` 엔티티 관리에 사용됩니다.
    -   `com.product.infra.inboxEvent.jpa.InboxEventJpaRepository` 인터페이스를 통해 구현되며, 이는 `core-inboxEvent`의 `InboxEventRepository`를 확장합니다.
-   **MyBatis** (의존성은 있으나, 현재 제공된 코드에서는 `InboxEvent` 처리에 직접적으로 사용되지 않음. 다른 용도로 사용될 수 있음)

### 2.3. 핵심 디자인 패턴

-   **Inbox 패턴**:
    -   수신된 메시지의 중복 처리를 방지하고 멱등성을 보장하기 위해 사용됩니다.
    -   메시지를 처리하기 전에 `InboxEvent` 테이블에 해당 메시지 ID(또는 고유 식별자)와 소비자 ID를 기록하여 이미 처리된 메시지인지 확인합니다.
    -   `InboxHelper` 및 `InboxEventJpaCommandServiceImpl`을 통해 구현됩니다.

### 2.4. 트랜잭션 관리

-   Spring의 `@Transactional` 어노테이션을 기반으로 선언적 트랜잭션 관리를 사용합니다.
-   커스텀 트랜잭션 어노테이션 (`@ProductAppReadTransactional`, `@ProductAppWriteTransactional`)을 정의하여, 특정 트랜잭션 매니저(`productAppTransactionManager`)를 사용하고 읽기 전용 여부, 전파 속성 등을 사전에 지정하여 서비스 계층에서 일관되고 간결하게 트랜잭션을 적용합니다.

## 3. 핵심 기능 상세

### 3.1. Kafka 메시지 수신 및 처리 (`com.product.kafka.listener.OrderListener`)

-   **리스너**: `@KafkaListener` 어노테이션을 사용하여 `event.product` 토픽을 구독합니다.
    -   `groupId`: `event.product`
    -   `concurrency`: "3" (3개의 컨슈머 스레드로 병렬 처리)
-   **메시지 처리 흐름**:
    1.  수신된 메시지(JSON 문자열)를 `PayloadHelper.parser()`를 사용하여 `ProductCreatedEventPayload` 객체로 변환합니다.
    2.  `ProductCreatedEventPayload`의 `aggregateId`와 리스너의 `consumerId`를 사용하여 `InboxEvent.newInstance()`로 `InboxEvent` 객체를 생성합니다.
    3.  `InboxHelper.isReceived()`를 호출하여 해당 `InboxEvent`가 이미 수신 및 처리되었는지 확인합니다.
        -   `InboxHelper`는 내부적으로 `InboxEventJpaCommandServiceImpl.isReceived()`를 호출합니다.
        -   `InboxEventJpaCommandServiceImpl`는 `InboxEventJpaRepository.existsByAggregateIdAndConsumerId()`로 중복 여부를 확인하고, 중복이 아니면 `InboxEventJpaRepository.save()`로 `InboxEvent`를 저장합니다.
    4.  중복 여부에 따라 적절한 로그를 남깁니다.
    5.  `Acknowledgment.acknowledge()`를 호출하여 메시지 처리가 완료되었음을 Kafka 브로커에 알립니다.

### 3.2. Inbox 패턴을 통한 멱등성 보장

-   **`com.product.helper.inbox.InboxHelper`**:
    -   `isReceived(InboxEvent inboxEvent, String consumerId)`:
        -   `InboxEventCommandService` (구현체: `InboxEventJpaCommandServiceImpl`)를 호출하여 이벤트 저장 및 중복 확인 로직을 수행합니다.
        -   `DataIntegrityViolationException` 발생 시, 원인 메시지에 "duplicate" 또는 "unique" 키워드가 포함되어 있으면 중복으로 간주하고 `false`를 반환합니다. 그 외의 DB 제약 조건 위반 시에는 예외를 다시 던집니다.
-   **`com.product.service.inboxEvent.command.jpa.InboxEventJpaCommandServiceImpl`**:
    -   `isReceived(InboxEvent inboxEvent, String consumerId)`:
        -   `@ProductAppWriteTransactional` 어노테이션으로 트랜잭션 내에서 실행됩니다.
        -   먼저 `inboxEventJpaRepository.existsByAggregateIdAndConsumerId()`를 통해 DB에 동일한 `aggregateId`와 `consumerId`를 가진 `InboxEvent`가 있는지 확인합니다.
        -   존재하면 `false` (이미 수신됨)를 반환합니다.
        -   존재하지 않으면 `inboxEventJpaRepository.save(inboxEvent)`를 통해 `InboxEvent`를 저장하고 `true` (처음 수신됨)를 반환합니다.

### 3.3. 페이로드 처리 (`com.product.helper.kafka.PayloadHelper`)

-   `parser(String payload, Class<T> payloadClass)`:
    -   수신된 Kafka 메시지(문자열)를 지정된 DTO 클래스(`ProductCreatedEventPayload`)로 변환합니다.
    -   내부적으로 `ObjectMapper`를 사용합니다.
    -   **특이사항**: `this.objectMapper.readValue(payload, String.class)`로 한 번 문자열로 변환한 후, 다시 `this.objectMapper.readValue(fixed, payloadClass)`로 최종 객체 변환을 수행합니다. 이는 Kafka 메시지 페이로드가 이중으로 직렬화되었거나, 문자열 내부에 이스케이프된 JSON이 포함된 경우를 처리하기 위함일 수 있습니다.

## 4. 설정 (Configuration)

### 4.1. 데이터베이스 설정 (`com.product.config.db`)

-   `ProductAppDBConfig`:
    -   `ProductAppDatasourceConfig` 내부 클래스를 통해 HikariCP 기반의 `DataSource` (빈 이름: `productAppDatasource`)를 설정합니다.
    -   `application-*.yml` 파일의 `spring.datasource.*` 속성을 참조합니다.
-   `ProductAppJpaConfig`:
    -   `LocalContainerEntityManagerFactoryBean` (빈 이름: `productAppEntityManagerFactory`)을 설정하여 JPA EntityManager를 구성합니다.
        -   `productAppDatasource`를 사용합니다.
        -   `com.inboxEvent.domain.entity` 패키지를 스캔하여 `InboxEvent` 엔티티를 인식합니다.
        -   `hibernate.hbm2ddl.auto` 속성을 `update`로 설정합니다.
    -   `@EnableJpaRepositories`를 통해 `com.product.infra` 패키지 내 JPA 리포지토리(`InboxEventJpaRepository`)를 스캔하고, `productAppEntityManagerFactory`와 `productAppTransactionManager`를 사용하도록 지정합니다.

### 4.2. Kafka 설정 (`com.product.config.kafka.KafkaConfig`)

-   `Consumer` 내부 클래스를 통해 Kafka `ConsumerFactory`와 `ConcurrentKafkaListenerContainerFactory` 빈을 설정합니다.
-   `application-*.yml` 파일의 `kafka.host`, `kafka.port` 속성을 참조하여 Kafka 브로커 정보를 설정합니다.
-   Key/Value Deserializer로 `StringDeserializer`를 사용합니다.
-   `ConcurrentKafkaListenerContainerFactory`에 `AckMode.MANUAL_IMMEDIATE`를 설정하여 수동 오프셋 커밋을 사용합니다.

### 4.3. 트랜잭션 설정 (`com.product.config.transactional`)

-   `ProductAppTransactionalConfig`:
    -   `JpaTransactionManager` (빈 이름: `productAppTransactionManager`)를 설정하며, `productAppEntityManagerFactory`를 주입받아 사용합니다.
    -   `@EnableTransactionManagement`를 통해 선언적 트랜잭션 관리를 활성화합니다.
-   커스텀 트랜잭션 어노테이션:
    -   `@ProductAppReadTransactional` (`com.product.config.transactional.annotaion.ProductAppReadTransactional`): 읽기 전용 트랜잭션에 사용. 기본 전파 속성은 `Propagation.REQUIRES_NEW`.
    -   `@ProductAppWriteTransactional` (`com.product.config.transactional.annotaion.ProductAppWriteTransactional`): 쓰기 트랜잭션에 사용. 기본적으로 `Exception.class`에 대해 롤백 수행하며, 기본 전파 속성은 `Propagation.REQUIRED`.
    -   두 어노테이션 모두 `productAppTransactionManager`를 사용하도록 지정되어 있습니다.

## 5. 테스트 전략

-   **단위/통합 테스트**:
    -   JPA 리포지토리 테스트 (`InboxEventJpaRepositoryTest`): `@SpringBootTest`와 `@ActiveProfiles`를 사용하여 `InboxEvent` 저장 기능을 테스트합니다. 각 테스트 전에 `deleteAll()`로 데이터를 초기화합니다.
    -   서비스 계층 테스트 (`InboxEventJpaCommandServiceImplTest`): `@SpringBootTest`와 `@ActiveProfiles`를 사용하여 `InboxEventCommandService`의 `isReceived` 메서드 및 `InboxHelper`를 통한 중복 저장 방지 로직을 테스트합니다.
-   **테스트 프로파일**:
    -   `application-app-product-kafka-test.yml`: H2 인메모리 데이터베이스를 사용하며, p6spy 로깅 및 트랜잭션 추적 로깅이 활성화되어 있습니다.
    -   `application-app-product-kafka-test_db.yml`: MySQL 데이터베이스를 사용하며, 유사한 로깅 설정이 되어 있습니다. (테스트 코드에서는 주로 `app-product-kafka-test` 프로파일 사용)
    -   테스트 클래스에서 `@ActiveProfiles(value = {"app-product-kafka-test"})` 어노테이션을 통해 해당 프로파일을 사용합니다.
-   **테스트용 애플리케이션 컨텍스트**:
    -   테스트 클래스에서 `@SpringBootTest`를 사용하면 `ProductAppKafkaApplication`에 정의된 설정을 기반으로 테스트 컨텍스트가 로드됩니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.product.kafka.listener`: Kafka 메시지 리스너.
    -   `com.product.infra.inboxEvent.jpa`: `InboxEvent` 관련 JPA 리포지토리.
    -   `com.product.service.inboxEvent.command`: `InboxEvent` 처리 관련 커맨드 서비스 인터페이스 및 JPA 구현체.
    -   `com.product.helper.inbox`: Inbox 패턴 관련 헬퍼 클래스.
    -   `com.product.helper.kafka`: Kafka 메시지 처리 관련 헬퍼 클래스 (토픽 정보, 페이로드 파서).
    -   `com.product.config`: 애플리케이션 설정 (DB, Kafka, Transaction).
-   **서비스 및 리포지토리 네이밍**:
    -   JPA 구현체는 `*JpaRepository` (리포지토리 인터페이스), `*JpaCommandServiceImpl` (서비스 구현체) 패턴을 따릅니다.