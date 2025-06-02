package com.product.helper.kafka;

/**
 * Kafka 관련 정보를 정의하는 클래스입니다.
 * Kafka 토픽 등의 상수를 포함하고 있습니다.
 */
public class KafkaInfo {
    /**
     * Kafka와 관련된 Topic 정보를 정의하는 static 클래스입니다.
     * Kafka Producer 또는 Consumer가 사용할 수 있는 토픽 이름 상수를 포함하고 있습니다.
     */
    public static class Topic {
        public static final String EVENT_PRODUCT = "event.product";
    }
}
