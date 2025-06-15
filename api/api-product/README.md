# `api/api-product` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`api/api-product` 모듈은 애플리케이션의 **핵심 비즈니스 로직 처리**와 **데이터 트랜잭션 관리**를 담당합니다. 주로 상품(Product) 데이터의 생성, 수정, 조회와 관련된 연산을 수행하며, 안정적인 이벤트 발행을 위해 Outbox 패턴을 구현하고 있습니다.

- 이 모듈은 트랜잭션 및 비즈니스 로직에 집중합니다.
- 모듈 내 `test` 패키지에 있는 `@SpringBootApplication` 클래스(`ProductAppApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다. 이 모듈을 의존하는 다른 모듈에서 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택 및 아키텍처

### 2.1. 데이터 영속성 (Data Persistence)

-   **JPA (Java Persistence API)**:
    -   주요 엔티티(`Product`, `Category`, `Stock`, `ProductOption`, `OutboxEvent`) 관리에 사용됩니다.
    -   `org.springframework.data.repository.Repository` 또는 `org.springframework.data.jpa.repository.JpaRepository`를 상속받는 인터페이스를 통해 구현됩니다 (예: `ProductJpaRepository`, `OutboxEventJpaRepository`).
-   **MyBatis**:
    -   JPA와 함께 사용되며, 주로 배치(batch) 작업이나 복잡한 동적 SQL 처리에 활용될 수 있습니다.
    -   `@Mapper` 어노테이션을 사용하여 매퍼 인터페이스를 정의합니다 (예: `ProductMybatisRepository`, `OutboxEventMybatisRepository`).
    -   JPA 리포지토리와 동일한 도메인 리포지토리 인터페이스를 구현하여 일관성을 유지합니다.

### 2.2. 메시징 (Messaging)

-   **Apache Kafka**:
    -   Outbox 패턴을 통해 발행된 이벤트를 비동기적으로 처리하기 위한 메시징 시스템으로 사용됩니다.
    -   `spring-kafka` 라이브러리를 활용하여 Kafka 프로듀서를 설정하고 메시지를 발행합니다.

### 2.3. 핵심 디자인 패턴

-   **Outbox 패턴**:
    -   데이터 변경(예: 상품 생성)과 해당 변경에 대한 이벤트 발행을 하나의 트랜잭션으로 묶어 데이터 일관성을 보장합니다.
    -   `OutboxEvent` 엔티티를 사용하여 이벤트를 DB에 먼저 기록한 후, 별도의 프로세스(`OutboxEventRelay`)가 이를 읽어 Kafka로 발행합니다.
-   **데코레이터 패턴 (Decorator Pattern)**:
    -   `OutboxEventDecoratorServiceImpl`에서 JPA와 MyBatis 기반의 `OutboxEventCommandService` 구현체를 조합하여, 단일 저장 시에는 JPA를, 다중 저장 시에는 MyBatis를 사용하는 유연성을 제공합니다.

### 2.4. 트랜잭션 관리

-   Spring의 `@Transactional` 어노테이션을 기반으로 선언적 트랜잭션 관리를 사용합니다.
-   커스텀 트랜잭션 어노테이션 (`@ProductReadTransactional`, `@ProductWriteTransactional`)을 정의하여, 특정 트랜잭션 매니저(`productTransactionManager`)를 사용하고 읽기 전용 여부, 전파 속성 등을 사전에 지정하여 서비스 계층에서 일관되고 간결하게 트랜잭션을 적용합니다.

## 3. 핵심 기능 상세

### 3.1. 상품 관리 (Product Management)

-   **엔티티**:
    -   `com.product.domain.entity.Category`: 상품 카테고리 정보.
    -   `com.product.domain.entity.Product`: 상품 기본 정보 (이름, 설명, 가격 등). `Category`와 연관 관계.
    -   `com.product.domain.entity.ProductOption`: 상품 옵션 정보 (예: 색상, 사이즈). `Product`와 연관 관계.
    -   `com.product.domain.entity.Stock`: 상품 옵션별 재고 수량. `ProductOption`과 연관 관계.
-   **리포지토리 (`com.product.infra.product.*`)**:
    -   JPA 구현체: `CategoryJpaRepository`, `ProductJpaRepository`, `ProductOptionJpaRepository`, `StockJpaRepository`.
    -   MyBatis 구현체: `CategoryMybatisRepository`, `ProductMybatisRepository`, `ProductOptionMybatisRepository`, `StockMybatisRepository`.
    -   각각 `com.product.domain.repository.*`에 정의된 도메인 리포지토리 인터페이스를 구현합니다.
-   **커맨드 서비스 (`com.product.service.product.command.jpa.ProductJapCommandServiceImpl`)**:
    -   `saveCategoryAndProductAndProductOptionAndStock` 메서드를 통해 카테고리, 상품, 상품 옵션, 재고 정보를 한 번의 요청으로 연관 관계를 설정하여 저장합니다.
    -   저장 후, 상품 생성에 대한 `OutboxEvent`를 생성하여 발행 준비를 합니다.

### 3.2. Outbox 패턴 구현

-   **목적**: 데이터베이스 변경과 메시지 발행 간의 원자성을 보장하여 분산 시스템 환경에서의 데이터 일관성을 유지합니다.
-   **흐름**:
    1.  **이벤트 생성 및 임시 저장**: `ProductJapCommandServiceImpl`에서 상품 데이터 저장 후, `OutboxEvent.create()`를 통해 `OutboxEvent` 객체를 생성합니다.
    2.  **Outbox 테이블 저장**: 생성된 `OutboxEvent`는 `OutboxEventDecoratorServiceImpl`를 통해 데이터베이스의 `outbox_event` 테이블에 `PENDING` 상태로 저장됩니다.
        -   단일 이벤트 저장 시: `OutboxEventJpaCommandServiceImpl` 사용.
        -   다중 이벤트 저장 시: `OutboxEventMybatisCommandServiceImpl` 사용.
    3.  **이벤트 폴링 및 발행 (`com.product.relay.OutboxEventRelay`)**:
        -   `@Scheduled` 어노테이션을 통해 주기적으로 실행됩니다.
        -   `OutboxEventJpaReadServiceImpl` (또는 MyBatis 구현체)를 사용하여 `PENDING` 상태의 `OutboxEvent`를 조회합니다.
        -   조회된 각 이벤트에 대해 등록된 `RelayProcess` 구현체들 (`List<RelayProcess>`)을 순회하며, `isSend()` 메서드를 통해 해당 이벤트를 처리할 수 있는 프로세스인지 확인합니다.
        -   적합한 `RelayProcess` (예: `KafkaRelayProcessImpl`)를 찾으면 `send()` 메서드를 호출하여 이벤트를 외부 시스템(Kafka)으로 발행합니다.
    4.  **상태 업데이트**: `KafkaRelayProcessImpl`의 `send()` 메서드 내에서 Kafka 발행 성공/실패 여부에 따라 `OutboxEvent`의 상태를 `PUBLISHED` 또는 `FAILED`로 업데이트합니다 (`OutboxEventMybatisCommandServiceImpl.updateStatus()` 사용).
-   **주요 컴포넌트**:
    -   `com.outboxEvent.domain.entity.OutboxEvent`: 발행될 이벤트의 정보를 담는 엔티티 (대상 애그리거잇 타입/ID, 이벤트 타입, 페이로드, 상태 등).
    -   `com.product.service.outboxEvent.*`: Outbox 이벤트의 조회, 저장, 수정을 담당하는 서비스 인터페이스 및 구현체들.
    -   `com.product.relay.RelayProcess`: 이벤트 발행 로직을 추상화한 인터페이스.
    -   `com.product.relay.app.impl.KafkaRelayProcessImpl`: `RelayProcess`의 Kafka 발행 구현체. `ProducerHelper`를 사용하여 메시지를 Kafka로 전송합니다.
    -   `com.product.helper.kafka.ProducerHelper`: Kafka 메시지 발행을 위한 유틸리티 클래스.

## 4. 설정 (Configuration)

### 4.1. 데이터베이스 설정 (`com.product.config.db`)

-   `ProductDBConfig`:
    -   `ProductDatasourceConfig` 내부 클래스를 통해 HikariCP 기반의 `DataSource` (빈 이름: `productDatasource`)를 설정합니다.
    -   `application-*.yml` 파일의 `spring.datasource.*` 속성을 참조합니다.
-   `ProductJpaConfig`:
    -   `LocalContainerEntityManagerFactoryBean` (빈 이름: `productEntityManagerFactory`)을 설정하여 JPA EntityManager를 구성합니다.
        -   `productDatasource`를 사용합니다.
        -   `com.product.domain.entity`, `com.outboxEvent.domain.entity` 패키지를 스캔하여 엔티티를 인식합니다.
        -   `hibernate.hbm2ddl.auto` 속성을 `update`로 설정합니다.
    -   `@EnableJpaRepositories`를 통해 `com.product.infra` 패키지 내 JPA 리포지토리를 스캔하고, `productEntityManagerFactory`와 `productTransactionManager`를 사용하도록 지정합니다.
-   `ProductMybatisConfig`:
    -   `SqlSessionFactory` (빈 이름: `productSessionFactory`)와 `SqlSessionTemplate` (빈 이름: `productSessionTemplate`)을 설정합니다.
        -   `productDatasource`를 사용합니다.
        -   MyBatis 설정으로 `mapUnderscoreToCamelCase`를 활성화합니다.
    -   `@MapperScan`을 통해 `com.product.infra.*.mybatis` 패키지 내 MyBatis 매퍼 인터페이스를 스캔하고, `productSessionFactory`를 사용하도록 지정합니다.

### 4.2. Kafka 설정 (`com.product.config.kafka.KafkaConfig`)

-   `Producer` 내부 클래스를 통해 Kafka `ProducerFactory`와 `KafkaTemplate` 빈을 설정합니다.
-   `application-*.yml` 파일의 `kafka.host`, `kafka.port` 속성을 참조하여 Kafka 브로커 정보를 설정합니다.
-   Key/Value Serializer로 `StringSerializer`를 사용합니다.

### 4.3. 트랜잭션 설정 (`com.product.config.transactional`)

-   `ProductTransactionalConfig`:
    -   `JpaTransactionManager` (빈 이름: `productTransactionManager`)를 설정하며, `productEntityManagerFactory`를 주입받아 사용합니다.
    -   `@EnableTransactionManagement`를 통해 선언적 트랜잭션 관리를 활성화합니다.
-   커스텀 트랜잭션 어노테이션:
    -   `@ProductReadTransactional`: 읽기 전용 트랜잭션에 사용. 기본 전파 속성은 `REQUIRES_NEW`.
    -   `@ProductWriteTransactional`: 쓰기 트랜잭션에 사용. 기본적으로 `Exception.class`에 대해 롤백 수행.
    -   두 어노테이션 모두 `productTransactionManager`를 사용하도록 지정되어 있습니다.

## 5. 테스트 전략

-   **단위/통합 테스트**:
    -   JPA 리포지토리 테스트: `@DataJpaTest`를 사용하여 슬라이스 테스트를 수행합니다.
    -   MyBatis 매퍼 테스트: `@SpringBootTest`를 사용하며, `QueryExecute` 헬퍼 클래스를 통해 테스트 전 데이터 초기화를 수행할 수 있습니다.
    -   서비스 계층 테스트: `@SpringBootTest`를 사용하여 통합 테스트를 수행합니다.
    -   `OutboxEventRelay` 및 `ProducerHelper`에 대한 테스트도 포함되어 있습니다.
-   **테스트 프로파일**:
    -   `application-api-product-test.yml`: H2 인메모리 데이터베이스를 사용합니다. JPA 테스트 등에 주로 활용됩니다.
    -   `application-api-product-test_db.yml`: MySQL 데이터베이스를 사용합니다. 실제 DB 연동이 필요한 통합 테스트에 활용됩니다.
    -   테스트 클래스에서 `@ActiveProfiles` 어노테이션을 통해 사용할 프로파일을 지정합니다.
-   **테스트용 애플리케이션 컨텍스트 (`com.product.ProductAppApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션과 `@ComponentScan`을 통해 테스트 실행에 필요한 빈들을 로드합니다.
    -   `README.md`에서 언급된 바와 같이, 이 클래스는 실제 애플리케이션 실행이 아닌 테스트 환경 구성을 위해 존재합니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.product.domain`: 도메인 엔티티 및 도메인 리포지토리 인터페이스.
    -   `com.product.infra`: 데이터 영속성 계층 구현체 (JPA, MyBatis).
    -   `com.product.service`: 비즈니스 로직을 담당하는 서비스 계층.
        -   `command`: 데이터 변경 관련 서비스.
        -   `read`: 데이터 조회 관련 서비스.
        -   `decorator`: 서비스 구현체를 조합/확장하는 데코레이터.
    -   `com.product.config`: 애플리케이션 설정 (DB, Kafka, Transaction).
    -   `com.product.relay`: Outbox 이벤트 발행 로직.
    -   `com.product.helper`: 유틸리티 클래스 (Kafka 관련).
    -   `com.outboxEvent.*`: Outbox 이벤트 관련 도메인, 인프라, 서비스 (별도 모듈 `core-outboxEvent`에서 정의된 것을 사용하는 것으로 보임).
-   **서비스 및 리포지토리 네이밍**:
    -   JPA 구현체는 `*JpaRepository` (인터페이스), `*JpaServiceImpl` (서비스 구현체) 패턴을 따릅니다.
    -   MyBatis 구현체는 `*MybatisRepository` (인터페이스), `*MybatisServiceImpl` (서비스 구현체) 패턴을 따릅니다.
    -   Outbox 이벤트 처리 서비스는 `outboxEventDecoratorServiceImpl`, `outboxEventJpaCommandServiceImpl`, `outboxEventMybatisCommandServiceImpl` 등 역할과 사용 기술을 명시적으로 네이밍합니다.

## 7. 주의사항 및 권장 사항

-   **`@SpringBootApplication` 사용**: `api/api-product` 모듈 내의 `ProductAppApplicationTest`는 테스트 목적으로만 사용해야 하며, 이 모듈을 의존하는 다른 모듈에서 애플리케이션 실행 진입점으로 사용해서는 안 됩니다.
-   **JPA vs MyBatis 선택**: 현재 구조상 두 가지 영속성 기술을 모두 지원하므로, 각 기술의 장점을 살릴 수 있는 상황에 맞게 선택하여 사용하는 것이 좋습니다. 예를 들어, 복잡한 연관관계 매핑이나 객체 그래프 탐색에는 JPA를, 벌크 연산이나 동적 쿼리에는 MyBatis를 고려할 수 있습니다. `OutboxEventDecoratorServiceImpl`처럼 특정 조건에 따라 다른 기술을 사용하는 것도 좋은 예시입니다.
-   **Outbox 패턴의 안정성**: `OutboxEventRelay`의 스케줄링 간격, 재시도 로직(현재 코드에는 명시적인 재시도 로직이 복잡하게 구현되어 있지는 않음), 실패 처리 정책 등을 실제 운영 환경에 맞게 조정하고 모니터링하는 것이 중요합니다.