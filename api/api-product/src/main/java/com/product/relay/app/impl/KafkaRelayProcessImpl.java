package com.product.relay.app.impl;


import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.outboxEvent.helper.MessageHelper;
import com.product.helper.kafka.ProducerHelper;
import com.product.relay.app.RelayProcess;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * KafkaRelayProcessImpl 클래스는 Kafka 시스템으로 메시지를 전송하는 로직을 구현한 클래스입니다.
 * RelayProcess 인터페이스를 구현하며, 특정 시스템으로 메시지를 전송할 수 있는지 확인하고,
 * 메시지를 Kafka를 통해 전송하며 그 결과를 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class KafkaRelayProcessImpl implements RelayProcess {
    private final OutboxEventCommandService outboxEventMybatisCommandServiceImpl;

    @Override
    public boolean isSend(String sourceSystem) {
        return MessageHelper.Kafka.SOURCE_SYSTEM.equals(sourceSystem);
    }

    @Override
    public void send(OutboxEvent outboxEvent) {
        ProducerHelper.getInstance().send(outboxEvent.getMessageKey(), outboxEvent.getPayload())
                .whenComplete((r, e) -> {
                    OutboxEventStatus outboxEventStatus = OutboxEventStatus.PUBLISHED;
                    if(Objects.nonNull(e)) {
                        outboxEventStatus = OutboxEventStatus.FAILED;
                    }

                    this.outboxEventMybatisCommandServiceImpl.updateStatus(List.of(outboxEvent), outboxEventStatus);
                });
    }
}
