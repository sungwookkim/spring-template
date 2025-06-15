# `core/core-member` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`core/core-member` 모듈은 애플리케이션의 핵심 도메인 개념 중 하나인 **회원(Member)을 정의**하는 것을 주요 목적으로 합니다. 이 모듈은 DDD(도메인 주도 설계)의 도메인 계층과 유사하게, `Member` 엔티티와 해당 엔티티를 다루기 위한 기본적인 리포지토리 인터페이스를 제공합니다.

- 이 모듈은 DDD의 도메인과 같은 주요 개념을 다루기 위한 모듈입니다.
- 모듈 내 `@SpringBootApplication` 어노테이션이 있는 클래스(`MemberCoreApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다.
- 이 모듈을 의존하는 다른 모듈에서는 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot Data JPA**: `Member` 엔티티를 정의하고 데이터베이스와 상호작용하기 위한 기본적인 어노테이션(`@Entity`, `@Table`, `@Id`, `@Column` 등)을 사용합니다.
-   **Lombok**: `@Getter`, `@NoArgsConstructor` 어노테이션을 사용하여 보일러플레이트 코드를 줄입니다.

## 3. 핵심 기능 상세

### 3.1. `Member` 엔티티 (`com.member.domain.entity.Member`)

-   **목적**: 회원의 기본 정보를 저장합니다.
-   **주요 필드**:
    -   `memberId` (Long): 데이터베이스의 기본 키 (자동 생성, `IDENTITY` 전략).
    -   `name` (String): 회원의 이름. `nullable = false`.
    -   `age` (Integer): 회원의 나이. `nullable = false`.
-   **생성자**:
    -   `Member(String name, Integer age)`: 이름과 나이를 받아 새로운 `Member` 인스턴스를 생성합니다. `memberId`는 데이터베이스에 저장될 때 자동 생성됩니다.
    -   `@NoArgsConstructor`: JPA 프록시 객체 생성 등을 위해 기본 생성자를 제공합니다.

### 3.2. `MemberRepository` 인터페이스 (`com.member.domain.repository.MemberRepository`)

-   **목적**: `Member` 엔티티에 대한 데이터 접근 로직 중, 도메인 특화적인 커스텀 쿼리 메서드를 정의합니다.
-   **주요 메서드**:
    -   `void save(Member member)`: `Member` 엔티티를 저장합니다.
    -   `Member findByName(String memberName)`: 주어진 이름으로 회원을 조회합니다.
-   **참고**: 이 인터페이스는 Spring Data JPA의 `JpaRepository`를 직접 상속하지 않습니다. 실제 JPA 연동은 이 모듈을 사용하는 애플리케이션 모듈(예: `api-member`)의 인프라 계층에서 `JpaRepository`와 함께 이 인터페이스를 상속받아 구현됩니다.

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `org.springframework.boot:spring-boot-starter-data-jpa`: JPA 관련 의존성을 포함합니다.
    -   `com.h2database:h2`: 런타임 시점에 H2 인메모리 데이터베이스를 사용할 수 있도록 의존성을 추가합니다 (주로 테스트 환경에서 활용).
-   이 모듈 자체에는 애플리케이션 실행을 위한 `application.yml` 등의 설정 파일이 포함되어 있지 않습니다. 데이터베이스 연결 정보 등은 이 모듈을 사용하는 애플리케이션 모듈에서 설정합니다.

## 5. 테스트 전략

-   **테스트용 애플리케이션 컨텍스트 (`com.member.MemberCoreApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 가지고 있어, 이 모듈 내의 컴포넌트(주로 엔티티)를 테스트 환경에서 Spring 컨텍스트에 로드하고 스캔하는 데 사용될 수 있습니다.
    -   `README.md`에서 강조된 바와 같이, 이 클래스는 실제 애플리케이션 실행 용도가 아닙니다.
-   이 모듈 자체에는 구체적인 단위 테스트나 통합 테스트 코드가 포함되어 있지 않습니다. `Member` 엔티티와 `MemberRepository` 인터페이스의 실제 동작 테스트는 이들을 사용하는 애플리케이션 모듈에서 수행됩니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.member.domain.entity`: 도메인 엔티티(`Member`)를 포함합니다.
    -   `com.member.domain.repository`: 도메인 리포지토리 인터페이스(`MemberRepository`)를 포함합니다.
-   **클래스 및 인터페이스 네이밍**:
    -   엔티티 클래스는 도메인 객체의 이름을 따릅니다 (예: `Member`).
    -   리포지토리 인터페이스는 `[엔티티명]Repository` 패턴을 따릅니다 (예: `MemberRepository`).