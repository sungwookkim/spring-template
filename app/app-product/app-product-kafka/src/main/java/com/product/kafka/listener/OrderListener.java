package com.product.kafka.listener;

import com.inboxEvent.domain.entity.InboxEvent;
import com.payload.ProductCreatedEventPayload;
import com.product.helper.inbox.InboxHelper;
import com.product.helper.kafka.KafkaInfo;
import com.product.helper.kafka.PayloadHelper;
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
    private final InboxHelper inboxHelper;

    @KafkaListener(topics = KafkaInfo.Topic.EVENT_PRODUCT
            , groupId = KafkaInfo.Group.EVENT_PRODUCT
            , concurrency = "3")
    public void orderEventListen(String message, Acknowledgment ack) {
        Thread currentThread = Thread.currentThread();
        String consumerId = "orderEventListen";

        ProductCreatedEventPayload productCreatedEventPayload = this.payloadHelper.parser(message, ProductCreatedEventPayload.class);
        InboxEvent inboxEvent = InboxEvent.newInstance(productCreatedEventPayload.aggregateId(), consumerId);

        if(inboxHelper.isReceived(inboxEvent, consumerId)) {
            log.info("=============== {} Received: {}", currentThread.getName(), productCreatedEventPayload);
        } else {
            log.info("=============== {} Duplicate Received: {}",  currentThread.getName(), productCreatedEventPayload);
        }

        ack.acknowledge();
    }
}
