# `core/core-payload` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`core/core-payload` 모듈은 애플리케이션 내에서 **이벤트 페이로드(payload)를 정의**하는 것을 주요 목적으로 합니다. 이 모듈은 이벤트 기반 아키텍처에서 사용될 다양한 이벤트의 데이터 구조를 정의하는 클래스들을 포함합니다.

현재 이 모듈에는 `ProductCreatedEventPayload` 레코드(record)가 정의되어 있으며, 이는 제품 생성 이벤트와 관련된 데이터를 담는 역할을 합니다.

- 이 모듈은 이벤트 페이로드와 같은 데이터 구조를 정의하는 데 집중합니다.
- 모듈 내 `@SpringBootApplication` 어노테이션이 있는 클래스(`PayloadCoreApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다.
- 이 모듈을 의존하는 다른 모듈에서는 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot **

## 3. 핵심 기능 상세

### 3.1. `ProductCreatedEventPayload` 레코드 (`com.payload.ProductCreatedEventPayload`)

-   **목적**: 제품 생성 이벤트와 관련된 데이터를 캡슐화합니다.
-   **주요 필드**:
    -   `aggregateId` (String): 이벤트의 대상이 되는 애그리거트(여기서는 제품)의 고유 식별자.
    -   `categoryId` (Long): 생성된 제품이 속한 카테고리의 식별자.
    -   `productId` (Long): 생성된 제품의 식별자.
-   **특징**:
    -   Java 레코드(record)로 정의되어 있어, 불변(immutable) 데이터 객체를 간결하게 표현합니다.
    -   생성자, getter, `equals()`, `hashCode()`, `toString()` 메서드가 컴파일러에 의해 자동으로 생성됩니다.
-   **사용 예시**:
    -   Outbox 패턴에서 `OutboxEvent`의 `payload` 필드에 이 객체를 JSON 형태로 직렬화하여 저장할 수 있습니다.
    -   Kafka와 같은 메시징 시스템을 통해 이벤트를 발행할 때, 이 객체를 메시지의 본문으로 사용할 수 있습니다.

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `java` 플러그인만 적용되어 있으며, Spring Boot 관련 의존성은 직접적으로 포함되어 있지 않습니다.
    -   `junit-bom` 및 `junit-jupiter`를 테스트 의존성으로 가집니다.
-   이 모듈 자체에는 애플리케이션 실행을 위한 `application.yml` 등의 설정 파일이 포함되어 있지 않습니다.

## 5. 테스트 전략

-   **테스트용 애플리케이션 컨텍스트 (`com.payload.PayloadCoreApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 가지고 있어, 이 모듈 내의 컴포넌트(현재는 페이로드 레코드만 존재)를 테스트 환경에서 Spring 컨텍스트에 로드하고 스캔하는 데 사용될 수 있습니다.
    -   `README.md`에서 강조된 바와 같이, 이 클래스는 실제 애플리케이션 실행 용도가 아닙니다.
-   이 모듈 자체에는 `ProductCreatedEventPayload` 레코드에 대한 구체적인 단위 테스트 코드가 포함되어 있지 않습니다. 페이로드 객체의 생성 및 사용에 대한 테스트는 이 모듈을 사용하는 애플리케이션 모듈(예: `app-product-kafka`에서 메시지 파싱 시)에서 간접적으로 수행될 수 있습니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.payload`: 페이로드 관련 클래스들을 포함합니다.
-   **클래스 및 인터페이스 네이밍**:
    -   페이로드 클래스는 `[이벤트명]Payload` 패턴을 따릅니다 (예: `ProductCreatedEventPayload`).
    -   Java 레코드를 사용하여 데이터 객체를 정의합니다.