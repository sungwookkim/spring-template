package com.product.helper.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer 편의를 위한 메서드.
 * Producer 메서드들은 {@link org.springframework.kafka.core.KafkaOperations}인터페이스를 참고해서 생성.
 */
@Component
public class ProducerHelper {
    private final ObjectMapper objectMapper;

    @Getter
    private final KafkaTemplate<String, String> kafkaTemplate;

    ProducerHelper(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 키나 파티션 없이 지정된 토픽에 데이터를 전송합니다.
     * @param topic 토픽
     * @param data 전송할 데이터
     * @return {@link SendResult}에 대한 Future
     */
    public <T> CompletableFuture<SendResult<String, String>> send(String topic, T data) {
        try {
            return this.kafkaTemplate.send(topic, this.objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("직렬화 실패", e);
        }
    }

    /**
     * {@link ProducerHelper}의 싱글톤 인스턴스를 제공하기 위해 사용되는 정적 내부 클래스입니다.
     * 이 클래스는 Spring의 {@link Component}로 표시되어 있어, Spring 컨테이너에 의해 관리될 수 있습니다.
     * 싱글톤 디자인 패턴은 애플리케이션의 생명 주기 동안 {@link ProducerHelper} 인스턴스가 단 하나만 존재하도록 보장합니다.
     */
    @Component
    public static class Singleton {
        @Getter
        private static ProducerHelper instance;

        Singleton(ProducerHelper producerHelper) {
            instance = producerHelper;
        }
    }
}
