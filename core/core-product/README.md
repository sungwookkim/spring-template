# `core/core-product` 모듈 가이드 문서

## 1. 모듈 개요

### 1.1. 목적 및 주요 책임

`core/core-product` 모듈은 애플리케이션의 **상품(Product) 관련 핵심 도메인 개념을 정의**하는 것을 주요 목적으로 합니다. 이 모듈은 DDD(도메인 주도 설계)의 도메인 계층과 유사하게, 상품과 관련된 엔티티들(`Category`, `Product`, `ProductOption`, `Stock`)과 해당 엔티티들을 다루기 위한 기본적인 리포지토리 인터페이스를 제공합니다.

이 모듈은 상품의 기본 정보, 카테고리 분류, 상품 옵션, 재고 관리 등 상품 도메인의 핵심적인 데이터 구조와 관계를 정의합니다.

- 이 모듈은 DDD의 도메인과 같은 주요 개념을 다루기 위한 모듈입니다.
- 모듈 내 `@SpringBootApplication` 어노테이션이 있는 클래스(`ProductCoreApplicationTest`)는 **테스트 실행 시 Spring Bean 등록 및 스캔을 위한 용도**이며, 실제 애플리케이션 구동 용도로 사용해서는 안 됩니다.
- 이 모듈을 의존하는 다른 모듈에서는 해당 테스트용 Main 클래스를 사용하지 않도록 주의해야 합니다.

## 2. 주요 기술 스택

-   **Java 21**
-   **Spring Boot Data JPA**: 엔티티를 정의하고 데이터베이스와 상호작용하기 위한 기본적인 어노테이션(`@Entity`, `@Table`, `@Id`, `@Column`, `@ManyToOne`, `@OneToMany`, `@OneToOne` 등)을 사용합니다.
-   **Lombok**: `@Getter`, `@NoArgsConstructor`, `@Setter` 어노테이션을 사용하여 보일러플레이트 코드를 줄입니다.

## 3. 핵심 기능 상세

### 3.1. 엔티티 상세

#### 3.1.1. `Category` 엔티티 (`com.product.domain.entity.Category`)

-   **목적**: 상품의 카테고리 정보를 나타냅니다.
-   **주요 필드**:
    -   `categoryId` (Long): 카테고리 ID (PK).
    -   `name` (String): 카테고리명.
    -   `products` (List<Product>): 해당 카테고리에 속한 상품 목록 (`@OneToMany` 양방향 관계).
-   **주요 메서드**:
    -   `Category(String name)`: 생성자.
    -   `addProduct(Product product)`: 카테고리에 상품을 추가하고, 상품 객체에도 해당 카테고리를 설정하여 양방향 연관관계를 관리합니다.

#### 3.1.2. `Product` 엔티티 (`com.product.domain.entity.Product`)

-   **목적**: 상품의 기본 정보를 나타냅니다.
-   **주요 필드**:
    -   `productId` (Long): 상품 ID (PK).
    -   `name` (String): 상품명.
    -   `description` (String): 상품 설명.
    -   `price` (int): 상품 가격.
    -   `category` (Category): 상품이 속한 카테고리 (`@ManyToOne` 양방향 관계).
    -   `productOptions` (List<ProductOption>): 해당 상품의 옵션 목록 (`@OneToMany` 양방향 관계).
-   **주요 메서드**:
    -   `Product(String name, String description, int price)`: 생성자.
    -   `setCategory(Category category)`: 상품의 카테고리를 설정하고, 이전 카테고리에서는 해당 상품을 제거하며, 새 카테고리에는 상품을 추가하여 양방향 연관관계를 관리합니다.
    -   `addProductOption(ProductOption productOption)`: 상품에 옵션을 추가하고, 옵션 객체에도 해당 상품을 설정하여 양방향 연관관계를 관리합니다.

#### 3.1.3. `ProductOption` 엔티티 (`com.product.domain.entity.ProductOption`)

-   **목적**: 상품의 개별 옵션(예: 색상, 사이즈) 정보를 나타냅니다.
-   **주요 필드**:
    -   `productOptionId` (Long): 상품 옵션 ID (PK).
    -   `optionName` (String): 옵션명 (예: "색상").
    -   `optionValue` (String): 옵션값 (예: "빨강").
    -   `product` (Product): 해당 옵션이 속한 상품 (`@ManyToOne` 양방향 관계).
    -   `stock` (Stock): 해당 상품 옵션의 재고 정보 (`@OneToOne` 양방향 관계). `@Setter`가 존재합니다.
-   **주요 메서드**:
    -   `ProductOption(String optionName, String optionValue)`: 생성자.
    -   `setProduct(Product product)`: 상품 옵션에 상품을 설정하고, 이전 상품에서는 해당 옵션을 제거하며, 새 상품에는 옵션을 추가하여 양방향 연관관계를 관리합니다.

#### 3.1.4. `Stock` 엔티티 (`com.product.domain.entity.Stock`)

-   **목적**: 특정 상품 옵션의 재고 수량을 나타냅니다.
-   **주요 필드**:
    -   `stockId` (Long): 재고 ID (PK).
    -   `quantity` (int): 재고 수량.
    -   `productOption` (ProductOption): 해당 재고가 연결된 상품 옵션 (`@OneToOne` 양방향 관계).
-   **주요 메서드**:
    -   `Stock(int quantity)`: 생성자.
    -   `setProductOption(ProductOption productOption)`: 재고에 상품 옵션을 설정하고, 상품 옵션 객체에도 해당 재고를 설정하여 양방향 연관관계를 관리합니다.

### 3.2. 리포지토리 인터페이스

각 엔티티에 대해 기본적인 `save` 메서드를 정의하는 리포지토리 인터페이스가 제공됩니다. 실제 구현은 이 모듈을 사용하는 애플리케이션 모듈의 인프라 계층에서 Spring Data JPA를 통해 이루어집니다.

-   `com.product.domain.repository.cateogry.CategoryRepository`
-   `com.product.domain.repository.product.ProductRepository`
-   `com.product.domain.repository.productOption.ProductOptionRepository`
-   `com.product.domain.repository.stock.StockRepository`

## 4. 설정 (Configuration)

-   **`build.gradle`**:
    -   `org.springframework.boot:spring-boot-starter-data-jpa`: JPA 관련 의존성을 포함합니다.
    -   `com.h2database:h2`: 런타임 시점에 H2 인메모리 데이터베이스를 사용할 수 있도록 의존성을 추가합니다 (주로 테스트 환경에서 활용).
-   이 모듈 자체에는 애플리케이션 실행을 위한 `application.yml` 등의 설정 파일이 포함되어 있지 않습니다. 데이터베이스 연결 정보 등은 이 모듈을 사용하는 애플리케이션 모듈에서 설정합니다.

## 5. 테스트 전략

-   **테스트용 애플리케이션 컨텍스트 (`com.product.ProductCoreApplicationTest`)**:
    -   `@SpringBootApplication` 어노테이션을 가지고 있어, 이 모듈 내의 컴포넌트(주로 엔티티)를 테스트 환경에서 Spring 컨텍스트에 로드하고 스캔하는 데 사용될 수 있습니다.
    -   `README.md`에서 강조된 바와 같이, 이 클래스는 실제 애플리케이션 실행 용도가 아닙니다.
-   이 모듈 자체에는 구체적인 단위 테스트나 통합 테스트 코드가 포함되어 있지 않습니다. 엔티티와 리포지토리 인터페이스의 실제 동작 테스트는 이들을 사용하는 애플리케이션 모듈에서 수행됩니다.

## 6. 주요 코드 구조 및 네이밍 컨벤션

-   **패키지 구조**:
    -   `com.product.domain.entity`: 도메인 엔티티들을 포함합니다.
    -   `com.product.domain.repository`: 도메인 리포지토리 인터페이스들을 각 엔티티별 하위 패키지에 포함합니다.
-   **클래스 및 인터페이스 네이밍**:
    -   엔티티 클래스는 도메인 객체의 이름을 따릅니다 (예: `Product`, `Category`).
    -   리포지토리 인터페이스는 `[엔티티명]Repository` 패턴을 따릅니다 (예: `ProductRepository`).
-   **상수 `CLASS_NAME`**: 각 엔티티 클래스 내부에 `public final static String CLASS_NAME = [클래스명].class.getSimpleName();` 형태로 클래스 이름을 상수로 정의하고 있습니다. 이는 로깅이나 메타데이터 처리 시 유용할 수 있습니다.