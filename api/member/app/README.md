# app 모듈
해당 모듈에선 트랜잭션 및 비지니스 로직과 관련된 부분들을 담당하는 모듈이다. 

@SpringBootApplication를 활용해서 실제 어플리케이션에 활용하는 용도로 사용은 금한다. 단, 테스트를
하기 위해서 Spring Bean을 이용하여야 하는데 Bean 등록 및 Scan를 위해 test 패키지에 @SpringBootApplication이 있는 Main 클래스가 존재하긴 한다.
앞서 말했듯이 해당 모듈를 의존 하는 모듈에선 해당 Main 클래스 사용을 하지 않도록 주의해야 한다.