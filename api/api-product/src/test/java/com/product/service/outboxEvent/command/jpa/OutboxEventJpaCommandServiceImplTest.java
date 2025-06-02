package com.product.service.outboxEvent.command.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import com.product.service.outboxEvent.read.OutboxEventReadService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@ActiveProfiles(value = {"api-product-test"})
@SpringBootTest
public class OutboxEventJpaCommandServiceImplTest {
    @Autowired
    OutboxEventCommandService outboxEventJpaCommandServiceImpl;

    @Autowired
    OutboxEventReadService outboxEventJpaReadServiceImpl;

    @Test
    @DisplayName("OutboxEvent 저장 및 ID로 조회 테스트")
    void save1() {
        // give
        Map<String, String> event1Payload = Map.of("orderId", "1", "amount", "1000");
        OutboxEvent event1 = OutboxEvent.create("Order", "order-123", "test", "test", event1Payload);

        // When
        this.outboxEventJpaCommandServiceImpl.save(event1);

        // Then
        Assertions.assertTrue(event1.getId() > 0);
    }

    @Test
    @DisplayName("OutboxEvent 상태 변경")
    void updateStatus1() {
        // give
        Map<String, String> event1Payload = Map.of("orderId", "1", "amount", "1000");
        OutboxEvent event1 = OutboxEvent.create("Order", "order-123", "test", "test", event1Payload);

        // When
        this.outboxEventJpaCommandServiceImpl.save(event1);
        List<OutboxEvent> byStatusOrderByCreatedAtAsc = this.outboxEventJpaReadServiceImpl.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        this.outboxEventJpaCommandServiceImpl.updateStatus(byStatusOrderByCreatedAtAsc, OutboxEventStatus.PUBLISHED);
    }
}
