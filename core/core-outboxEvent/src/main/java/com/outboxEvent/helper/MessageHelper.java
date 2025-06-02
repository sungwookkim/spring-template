package com.outboxEvent.helper;

/**
 * 메시지 처리를 위한 도우미 클래스.
 * 이 클래스는 Kafka와 관련된 상수를 관리하며, 메시징 처리에 필요한 정적 값을 제공한다.
 */
public class MessageHelper {
    /**
     * Kafka와 관련된 상수를 정의하는 클래스.
     * 이 클래스는 Kafka를 사용하면서 공통적으로 참조되는 값을 제공한다.
     */
    public static class Kafka {
        public static final String SOURCE_SYSTEM = "kafka";

        /**
         * Kafka 메시지 키를 정의하는 클래스.
         * 이 클래스는 Kafka 메시지에서 사용되는 키 값을 상수로 제공한다.
         */
        public static class MessageKey {
            public static final String EVENT_PRODUCT = "event.product";
        }
    }
}
