# `web/web-member` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`web/web-member` 모듈은 **회원(Member) 관련 웹 API 엔드포인트를 제공**하는 것을 주요 목적으로 합니다. 이 모듈은 HTTP 요청을 받아 회원 정보의 생성 및 조회를 처리하고, 그 결과를 HTTP 응답으로 반환합니다.

주요 책임은 다음과 같습니다:

-   회원 생성을 위한 RESTful API (POST) 제공
-   회원 조회를 위한 RESTful API (GET) 제공
-   API 버전 관리 (v1, v2)
-   요청 데이터 유효성 검증
-   서비스 계층(`api-member` 모듈)과의 연동을 통한 비즈니스 로직 처리
-   표준화된 응답 형식(`RestResponseResult`) 사용
-   전역 예외 처리

### 1.2. `README.md` 핵심 내용 (추정)

-   이 모듈은 회원 관련 웹 API 인터페이스를 제공하는 데 집중합니다.
-   실제 애플리케이션 실행은 이 모듈의 `MemberApiApplication`을 통해 이루어집니다.
-   테스트는 `@WebMvcTest`를 활용하여 컨트롤러 계층에 대한 슬라이스 테스트를 수행합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot**:
    -   `spring-boot-starter-web`: Spring MVC를 사용하여 RESTful API를 구축합니다.
    -   `spring-boot-starter-thymeleaf`: (현재 직접적인 사용은 보이지 않으나, 의존성에 포함되어 있어 향후 UI 렌더링에 사용될 수 있습니다.)
    -   `spring-boot-starter-webflux`: (현재 직접적인 사용은 보이지 않으나, 의존성에 포함되어 있어 향후 비동기/논블로킹 API에 사용될 수 있습니다.)
    -   `spring-boot-starter-data-jpa`: (의존성에 포함되어 있으나, 이 모듈에서 직접 JPA 리포지토리를 정의하거나 사용하지는 않고, `api-member` 모듈을 통해 간접적으로 활용합니다.)
-   **Lombok**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Getter` 어노테이션을 사용하여 보일러플레이트 코드를 줄입니다.
-   **JUnit 5 / MockMvc**: 컨트롤러 테스트에 사용됩니다.

## 3. 핵심 기능 상세

### 3.1. API 버전 관리

이 모듈은 API 버전 관리를 위해 URL 경로에 버전 번호(v1, v2)를 포함하는 방식을 사용합니다.

-   **v1 API**:
    -   컨트롤러 인터페이스: `com.member.api.member.v1.MemberV1Controller`
    -   구현체: `com.member.api.member.v1.MemberV1MybatisControllerImpl`
    -   기본 경로: `/v1`
    -   주로 MyBatis를 사용하는 서비스(`memberMybatisCommandServiceImpl`, `memberMybatisReadServiceImpl`)와 연동됩니다.
-   **v2 API**:
    -   컨트롤러 인터페이스: `com.member.api.member.v2.MemberV2Controller` ( `MemberV1Controller` 상속)
    -   구현체: `com.member.api.member.v2.MemberV2JpaControllerImpl`
    -   기본 경로: `/v2`
    -   주로 JPA를 사용하는 서비스(`memberJpaCommandServiceImpl`, `memberJpaReadServiceImpl`)와 연동됩니다.
    -   v2 컨트롤러는 v1의 MyBatis 컨트롤러(`memberV1MybatisControllerImpl`)를 주입받아, `/v2/v1/member` 경로로 들어오는 v1 요청을 위임 처리합니다.

### 3.2. 회원 생성 API

-   **v1**: `POST /v1/member`
    -   요청 본문: `ReqV1MemberSave` (`name`, `age`)
    -   처리: `MemberV1MybatisControllerImpl.save()`
        1.  `ReqV1MemberSave.validate()`를 통해 요청 데이터 유효성 검증.
        2.  `Member` 엔티티 생성.
        3.  `memberMybatisCommandServiceImpl.save()` 호출하여 회원 정보 저장.
        4.  `RestResponseResult.success()` 반환.
-   **v2**: `POST /v2/member`
    -   요청 본문: `ReqV2MemberSave` (`name`, `age`)
    -   처리: `MemberV2JpaControllerImpl.save()`
        1.  `ReqV2MemberSave.validate()`를 통해 요청 데이터 유효성 검증.
        2.  `Member` 엔티티 생성.
        3.  `memberJpaCommandServiceImpl.save()` 호출하여 회원 정보 저장.
        4.  `RestResponseResult.success()` 반환.
-   **v2 (v1 위임)**: `POST /v2/v1/member`
    -   요청 본문: `ReqV1MemberSave`
    -   처리: `MemberV2JpaControllerImpl.save(ReqV1MemberSave)`
        -   `memberV1MybatisControllerImpl.save(reqSave)`를 호출하여 v1 컨트롤러의 로직으로 위임.

### 3.3. 회원 조회 API

-   **v1**: `GET /v1/member?name={name}`
    -   요청 파라미터: `name`
    -   처리: `MemberV1MybatisControllerImpl.findMember()`
        1.  `name` 파라미터 유효성 검증 (`StringUtils.notBlankAndNotEmptyValidate`).
        2.  `memberMybatisReadServiceImpl.findByName()` 호출하여 회원 정보 조회.
        3.  조회 결과를 `RespV1FindMember`로 매핑. 결과가 없으면 `RespV1FindMember.empty()` 사용.
        4.  `RestResponseResult.success(respV1FindMember)` 반환.
-   **v2**: `GET /v2/member?name={name}`
    -   요청 파라미터: `name`
    -   처리: `MemberV2JpaControllerImpl.v2findMember()`
        1.  `name` 파라미터 유효성 검증 (`StringUtils.notBlankAndNotEmptyValidate`).
        2.  `memberJpaReadServiceImpl.findByName()` 호출하여 회원 정보 조회.
        3.  조회 결과를 `RespV2FindMember`로 매핑. 결과가 없으면 `RespV2FindMember.empty()` 사용.
        4.  `RestResponseResult.success(respV2FindMember)` 반환.
-   **v2 (v1 위임 - 미지원)**: `GET /v2/v1/member?name={name}`
    -   처리: `MemberV2Controller.findMember()` (default 메서드)
        -   `ProcessException(ProcessCode.Common.UNSUPPORTED_FEATURE)`을 발생시켜 미지원 기능임을 알립니다.

### 3.4. 요청/응답 모델

-   **요청 모델**:
    -   `com.member.api.member.v1.model.MemberV1Model.ReqV1MemberSave`
    -   `com.member.api.member.v2.model.MemberV2Model.ReqV2MemberSave`
    -   각 요청 모델은 `validate()` 메서드를 통해 자체적으로 유효성 검증 로직을 포함합니다.
        -   `name`: 비어있거나 공백만 있는지 검증.
        -   `age`: null이 아니고 0보다 큰지 검증.
        -   유효성 검증 실패 시 `ProcessException` 발생.
-   **응답 모델**:
    -   `com.member.api.member.v1.model.MemberV1Model.RespV1FindMember` (record)
    -   `com.member.api.member.v2.model.MemberV2Model.RespV2FindMember` (record)
    -   각 응답 모델은 `empty()` 정적 팩토리 메서드를 제공하여, 조회 결과가 없을 때 반환할 기본 객체를 생성합니다.
-   **표준 응답 래퍼**: `com.member.common.http.RestResponseResult<T>`
    -   모든 API 응답은 이 클래스로 래핑되어 반환됩니다.
    -   `httpStatus` (커스텀 `HttpStatus` 내부 클래스), `processCode`, `processValue` (실제 데이터)를 포함합니다.
    -   성공 시 `RestResponseResult.success()` 또는 `RestResponseResult.success(value)` 정적 팩토리 메서드를 사용합니다.

### 3.5. 예외 처리

-   **`com.member.common.exception.ProcessException`**:
    -   애플리케이션 내에서 예상된 비즈니스 예외 처리에 사용됩니다.
    -   `ProcessCode`를 인자로 받아 예외를 생성하며, `resultCode`와 `processCode`를 가집니다.
-   **`com.member.spring.handler.RestExceptionHandler`**:
    -   `@RestControllerAdvice`를 사용하여 전역적으로 예외를 처리합니다.
    -   `ProcessException` 발생 시:
        -   HTTP 상태 코드 503 (Service Unavailable)과 함께 `RestResponseResult`로 응답. `processCode`는 `ProcessException`의 `processCode`를 사용.
    -   그 외 `Exception` 발생 시:
        -   HTTP 상태 코드 503 (Service Unavailable)과 함께 `RestResponseResult`로 응답. `processCode`는 `ProcessCode.Common.APPLICATION_EXCEPTION` 사용.
    -   Spring MVC 내부 예외 (`handleExceptionInternal` 오버라이드):
        -   HTTP 상태 코드 400 (Bad Request) 등 Spring에서 발생한 HTTP 관련 예외를 `RestResponseResult`로 래핑하여 응답. `processCode`는 `ProcessCode.Common.HTTP_STATUS_EXCEPTION` 사용.

### 3.6. 유틸리티

-   **`com.member.common.helper.lang.StringUtils`**:
    -   문자열 검증 유틸리티 (`isNotBlank`, `isBlank`, `isNotEmpty`, `isEmpty`).
    -   `notBlankAndNotEmptyValidate(Object value)`: `Optional`과 함수형 인터페이스를 사용하여 객체 값을 문자열로 변환 후, 비어있거나 공백만 있는지 검증하여 유효하면 해당 문자열을, 아니면 빈 문자열을 반환합니다.

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `core-member`, `api-member` 프로젝트 의존성 추가.
    -   Spring Boot 웹, Thymeleaf, WebFlux, Data JPA 스타터 의존성 추가.
-   **`com.member.MemberApiApplication`**:
    -   `@SpringBootApplication`을 통해 애플리케이션을 실행하고 자동 설정을 활성화합니다.
    -   `@ComponentScan("com.member")` (테스트 코드에서는 사용, 실제 애플리케이션에서는 `@SpringBootApplication`에 의해 기본적으로 현재 패키지 및 하위 패키지가 스캔됨).
-   **`com.member.spring.config.WebConfigController`**:
    -   `favicon.ico` 요청을 처리하기 위한 간단한 컨트롤러.
-   **`application-web-member-local.yml`**:
    -   서버 포트: 9090
    -   Spring Boot 기본 에러 페이지(whitelabel) 비활성화.

## 5. 테스트 전략

-   **`com.member.api.member.MemberControllerTest`**:
    -   `@WebMvcTest`: 웹 계층(컨트롤러)에 대한 슬라이스 테스트를 수행합니다. 서비스 계층은 Mock 처리되거나, `@ComponentScan`을 통해 실제 빈을 주입받아 통합 테스트 형태로 진행될 수 있습니다.
    -   `@ComponentScan("com.member")`: `com.member` 패키지 전체를 스캔하여 컨트롤러 및 관련 빈들을 로드합니다. (주의: `@WebMvcTest`는 기본적으로 지정된 컨트롤러만 스캔하므로, 서비스나 다른 컴포넌트를 함께 테스트하려면 `@ComponentScan`이나 `@Import`가 필요합니다.)
    -   `@ActiveProfiles("web-member-test")`: `application-web-member-test.yml` 프로파일을 활성화합니다.
    -   `MockMvc`: HTTP 요청을 시뮬레이션하고 응답을 검증합니다.
    -   `ObjectMapper`: 요청 본문을 JSON으로 직렬화하는 데 사용됩니다.
    -   다양한 시나리오(정상 케이스, 예외 케이스 - 필수 값 누락, 빈 값 등)에 대한 테스트 케이스를 포함합니다.
    -   JSON Path를 사용하여 응답 본문의 특정 필드 값을 검증합니다.
-   **`application-web-member-test.yml`**:
    -   H2 인메모리 데이터베이스 설정.
    -   JPA `open-in-view: false` 설정.
    -   P6Spy 로깅 활성화.
    -   Spring 트랜잭션 추적 로깅 활성화.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.member.api.member.v[버전]` : 버전별 API 컨트롤러 및 모델 클래스.
    -   `com.member.common.http`: HTTP 응답 관련 공통 클래스 (`RestResponseResult`, `ProcessCode`).
    -   `com.member.common.exception`: 커스텀 예외 클래스 (`ProcessException`).
    -   `com.member.common.helper.lang`: 문자열 유틸리티.
    -   `com.member.spring.config`: Spring 웹 설정 관련.
    -   `com.member.spring.handler`: 전역 예외 핸들러.
-   **컨트롤러 네이밍**:
    -   인터페이스: `MemberV[버전]Controller`
    -   구현체: `MemberV[버전][데이터접근기술]ControllerImpl` (예: `MemberV1MybatisControllerImpl`, `MemberV2JpaControllerImpl`)
-   **요청/응답 모델 네이밍**:
    -   `ReqV[버전]MemberSave`, `RespV[버전]FindMember`
-   **ProcessCode 네이밍**:
    -   버전별 모델 클래스 내부에 Enum으로 정의 (예: `MemberV1Model.MemberV1ProcessCode`).
    -   공통 ProcessCode는 `ProcessCode.Common` Enum으로 정의.