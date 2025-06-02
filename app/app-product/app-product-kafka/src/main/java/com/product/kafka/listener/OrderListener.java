package com.product.kafka.listener;

import com.payload.ProductCreatedEventPayload;
import com.product.kafka.helper.KafkaInfo;
import com.product.kafka.helper.PayloadHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {
    private final PayloadHelper payloadHelper;

    @KafkaListener(topics = KafkaInfo.Topic.EVENT_PRODUCT
            , groupId = KafkaInfo.Group.EVENT_PRODUCT
            , concurrency = "3")
    public void orderEventListen(String message, Acknowledgment ack) {
        ProductCreatedEventPayload productCreatedEventPayload = this.payloadHelper.parser(message, ProductCreatedEventPayload.class);
        log.info("Received: {}", productCreatedEventPayload);
        ack.acknowledge();
    }
}
