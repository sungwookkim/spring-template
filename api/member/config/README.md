# config 모듈
해당 모듈에선 접속 정보와 관련된 Config들을 관리한다. 여기서 접속 정보라 함은 Datasource와 같이 DB에 접근해야하는 설정 정보들을 뜻한다.
이와 같이 하는 이유는 어플리케이션에서 외부 접속과의 결합을 낮추기 위함이다.

DB 트랜잭션과 같은 경우 해당 모듈에선 Datasource만 제공하고 이를 의존하는 모듈에서 어떤 방식으로 쓸건지 결정한다.
이와 같이 하게 되면 DB 변경이 발생 하여도 의존하고 있는 모듈에선 변경된 모듈로 의존을 변경하기만 하면 되기 때문이다.

주의해야 할 점은 하나의 모듈에서 여러 개의 모듈을 의존할 수 있기 때문에 Bean 이름이 중복되지 않게 해야 한다.

