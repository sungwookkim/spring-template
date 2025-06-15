# `api/api-member` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`api/api-member` 모듈은 애플리케이션의 회원(Member) 관련 **핵심 비즈니스 로직 처리**와 **데이터 트랜잭션 관리**를 담당합니다. 주로 회원 데이터의 생성, 수정, 조회와 관련된 연산을 수행합니다.

* 이 모듈은 트랜잭션 및 비즈니스 로직에 집중합니다.

-   모듈 내 `test` 패키지에 있는 `@SpringBootApplication` 클래스(`MemberAppApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다. 이 모듈을 의존하는 다른 모듈에서 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택 및 아키텍처

### 2.1. 데이터 영속성 (Data Persistence)

-   **JPA (Java Persistence API)**:
    -   `Member` 엔티티 관리에 사용됩니다.
    -   `org.springframework.data.repository.Repository`를 상속받는 `MemberJpaRepository` 인터페이스를 통해 구현됩니다.
-   **MyBatis**:
    -   JPA와 함께 사용되며, `Member` 엔티티에 대한 CRUD 연산을 지원합니다.
    -   `@Mapper` 어노테이션을 사용하여 `MemberMybatisRepository` 매퍼 인터페이스를 정의합니다.
    -   JPA 리포지토리와 동일한 `MemberRepository` 도메인 리포지토리 인터페이스를 구현하여 일관성을 유지합니다.

### 2.2. 트랜잭션 관리

-   Spring의 `@Transactional` 어노테이션을 기반으로 선언적 트랜잭션 관리를 사용합니다.
-   커스텀 트랜잭션 어노테이션 (`@MemberReadTransactional`, `@MemberWriteTransactional`)을 정의하여, 특정 트랜잭션 매니저(`memberTransactionManager`)를 사용하고 읽기 전용 여부, 전파 속성 등을 사전에 지정하여 서비스 계층에서 일관되고 간결하게 트랜잭션을 적용합니다.

## 3. 핵심 기능 상세

### 3.1. 회원 관리 (Member Management)

-   **엔티티**:
    -   `com.member.domain.entity.Member`: 회원 기본 정보 (이름, 나이 등).
-   **리포지토리 (`com.member.infra.member.*`)**:
    -   JPA 구현체: `com.member.infra.member.jpa.MemberJpaRepository`.
    -   MyBatis 구현체: `com.member.infra.member.mybatis.MemberMybatisRepository`.
    -   두 구현체 모두 `com.member.domain.repository.MemberRepository` 인터페이스를 구현합니다.
-   **커맨드 서비스 (`com.member.service.member.command.*`)**:
    -   `MemberCommandService` 인터페이스와 그 구현체들 (`MemberJpaCommandServiceImpl`, `MemberMybatisCommandServiceImpl`)이 회원 정보 저장(`save`) 및 수정(`update`) 기능을 제공합니다.
    -   `MemberJpaCommandServiceImpl`의 `update`는 현재 지원하지 않는 기능으로 `IllegalStateException`을 발생시킵니다.
-   **조회 서비스 (`com.member.service.member.read.*`)**:
    -   `MemberReadService` 인터페이스와 그 구현체들 (`MemberJpaReadServiceImpl`, `MemberMybatisReadServiceImpl`)이 회원 이름으로 조회(`findByName`)하는 기능을 제공합니다.

## 4. 설정 (Configuration)

### 4.1. 데이터베이스 설정 (`com.member.config.db`)

-   `MemberDBConfig`:
    -   `MemberDatasourceConfig` 내부 클래스를 통해 HikariCP 기반의 `DataSource` (빈 이름: `memberDatasource`)를 설정합니다.
    -   `application-*.yml` 파일의 `spring.datasource.*` 속성을 참조합니다.
-   `MemberJpaConfig`:
    -   `LocalContainerEntityManagerFactoryBean` (빈 이름: `memberEntityManagerFactory`)을 설정하여 JPA EntityManager를 구성합니다.
        -   `memberDatasource`를 사용합니다.
        -   `com.member.domain.entity` 패키지를 스캔하여 엔티티를 인식합니다.
        -   `hibernate.hbm2ddl.auto` 속성을 `update`로 설정합니다.
    -   `@EnableJpaRepositories`를 통해 `com.member.infra` 패키지 내 JPA 리포지토리를 스캔하고, `memberEntityManagerFactory`와 `memberTransactionManager`를 사용하도록 지정합니다.
-   `MemberMybatisConfig`:
    -   `SqlSessionFactory` (빈 이름: `memberSessionFactory`)와 `SqlSessionTemplate` (빈 이름: `memberSessionTemplate`)을 설정합니다.
        -   `memberDatasource`를 사용합니다.
        -   MyBatis 설정으로 `mapUnderscoreToCamelCase`를 활성화하고, `com.member.infra.mybatis`를 타입 별칭 패키지로 설정합니다.
    -   `@MapperScan`을 통해 `com.member.infra.*.mybatis` 패키지 내 MyBatis 매퍼 인터페이스를 스캔하고, `memberSessionFactory`를 사용하도록 지정합니다.

### 4.2. 트랜잭션 설정 (`com.member.config.transactional`)

-   `MemberTransactionalConfig`:
    -   `JpaTransactionManager` (빈 이름: `memberTransactionManager`)를 설정하며, `memberEntityManagerFactory`를 주입받아 사용합니다.
    -   `@EnableTransactionManagement`를 통해 선언적 트랜잭션 관리를 활성화합니다.
-   커스텀 트랜잭션 어노테이션:
    -   `@MemberReadTransactional` (`com.member.config.transactional.annotaion.MemberReadTransactional`): 읽기 전용 트랜잭션에 사용. 기본 전파 속성은 `REQUIRES_NEW`.
    -   `@MemberWriteTransactional` (`com.member.config.transactional.annotaion.MemberWriteTransactional`): 쓰기 트랜잭션에 사용. 기본적으로 `Exception.class`에 대해 롤백 수행하며, 기본 전파 속성은 `REQUIRED`.
    -   두 어노테이션 모두 `memberTransactionManager`를 사용하도록 지정되어 있습니다.

## 5. 테스트 전략

-   **단위/통합 테스트**:
    -   JPA 리포지토리 테스트 (`MemberJpaRepositoryTest`): `@DataJpaTest`를 사용하여 슬라이스 테스트를 수행합니다.
    -   MyBatis 매퍼 테스트 (`MemberMybatisRepositoryTest`): `@SpringBootTest`를 사용하며, `QueryExecute` 헬퍼 클래스를 통해 테스트 전 데이터(`member` 테이블)를 삭제합니다.
    -   서비스 계층 테스트 (`MemberJpaReadServiceImplTest`, `MemberMybatisReadServiceImplTest`, `MemberJpaCommandServiceImplTest`, `MemberMybatisCommandServiceImplTest`): `@SpringBootTest`를 사용하여 통합 테스트를 수행하며, `QueryExecute`를 통해 테스트 전 데이터를 초기화합니다.
-   **테스트 프로파일**:
    -   `application-api-member-test.yml`: H2 인메모리 데이터베이스를 사용하며, p6spy 로깅 및 트랜잭션 추적 로깅이 활성화되어 있습니다.
    -   테스트 클래스에서 `@ActiveProfiles(value = {"api-member-test"})` 어노테이션을 통해 해당 프로파일을 사용합니다.
-   **테스트용 애플리케이션 컨텍스트 (`com.member.MemberAppApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 통해 테스트 실행에 필요한 빈들을 로드합니다.
    -   `README.md`에서 언급된 바와 같이, 이 클래스는 실제 애플리케이션 실행이 아닌 테스트 환경 구성을 위해 존재합니다.
-   **테스트 유틸리티 (`com.member.QueryExecute`)**:
    -   테스트 시 직접 SQL 쿼리를 실행하기 위한 헬퍼 클래스입니다. `DataSource`를 주입받아 사용합니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.member.domain`: 도메인 엔티티(`Member`) 및 도메인 리포지토리 인터페이스(`MemberRepository`).
    -   `com.member.infra`: 데이터 영속성 계층 구현체 (JPA, MyBatis).
        -   `jpa`: JPA 관련 리포지토리.
        -   `mybatis`: MyBatis 관련 매퍼.
    -   `com.member.service`: 비즈니스 로직을 담당하는 서비스 계층.
        -   `command`: 데이터 변경 관련 서비스 (JPA, MyBatis 구현체 분리).
        -   `read`: 데이터 조회 관련 서비스 (JPA, MyBatis 구현체 분리).
    -   `com.member.config`: 애플리케이션 설정 (DB, Transaction).
-   **서비스 및 리포지토리 네이밍**:
    -   JPA 구현체는 `*JpaRepository` (리포지토리 인터페이스), `*JpaServiceImpl` (서비스 구현체) 패턴을 따릅니다.
    -   MyBatis 구현체는 `*MybatisRepository` (매퍼 인터페이스), `*MybatisServiceImpl` (서비스 구현체) 패턴을 따릅니다.

## 7. 주의사항 및 권장 사항

-   **`@SpringBootApplication` 사용**: `api/api-member` 모듈 내의 `MemberAppApplicationTest`는 테스트 목적으로만 사용해야 하며, 이 모듈을 의존하는 다른 모듈에서 애플리케이션 실행 진입점으로 사용해서는 안 됩니다.
-   **JPA vs MyBatis 선택**: 현재 구조상 두 가지 영속성 기술을 모두 지원하므로, 각 기술의 장점을 살릴 수 있는 상황에 맞게 선택하여 사용하는 것이 좋습니다.
    -   `MemberJpaCommandServiceImpl`의 `update` 메서드는 현재 구현되어 있지 않으므로, JPA를 통한 업데이트 기능이 필요하다면 추가 구현이 필요합니다.
-   **`QueryExecute` 유틸리티**: 테스트 코드에서 `QueryExecute`를 사용하여 직접 SQL을 실행하는 것은 특정 상황(예: 테스트 데이터 준비/삭제)에서는 유용할 수 있으나, 과도하게 사용될 경우 테스트가 특정 DB 스키마나 데이터에 강하게 결합될 수 있습니다. 가능한 경우 리포지토리나 서비스 계층을 통한 데이터 조작을 테스트하는 것이 좋습니다.
-   **커스텀 트랜잭션 어노테이션의 전파 속성**:
    -   `@MemberReadTransactional`의 기본 전파 속성이 `Propagation.REQUIRES_NEW`로 설정되어 있습니다. 이는 읽기 작업임에도 불구하고 호출될 때마다 새로운 트랜잭션을 시작하게 만듭니다. 일반적인 읽기 작업에서는 `Propagation.SUPPORTS`나 `Propagation.REQUIRED` (기존 트랜잭션에 참여)가 더 적합할 수 있습니다. `REQUIRES_NEW`는 특정 격리 수준이나 독립적인 작업 단위가 필요한 경우에 신중하게 사용되어야 합니다.