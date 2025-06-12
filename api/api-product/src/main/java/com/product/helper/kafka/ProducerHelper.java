package com.product.helper.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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
    @Getter
    private static volatile ProducerHelper instance;

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

    @PostConstruct
    private void setInstance() {
        instance = this;
    }
}
