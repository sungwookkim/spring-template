package com.product.helper.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@ActiveProfiles(value = {"api-product-test"})
@SpringBootTest
public class ProducerHelperTest {

    @Test
    @DisplayName("event.order 테스트 발송")
    void eventOrderSend() {
        ProducerHelper producerHelper = ProducerHelper.Singleton.getInstance();

        Map<String, String> testValue = Map.of("test", "test");
        producerHelper.send(KafkaInfo.Topic.EVENT_PRODUCT, testValue);
    }
}
