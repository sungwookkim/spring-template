package com.product.service.outboxEvent.read.mybatis;

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
public class OutboxEventMybatisReadServiceImplTest {
    @Autowired
    OutboxEventReadService outboxEventMybatisReadServiceImpl;

    @Autowired
    OutboxEventCommandService outboxEventMybatisCommandServiceImpl;

    @Test
    @DisplayName("OutboxEvent 상태 조회")
    void 발송_대상_조회() {
        // give
        Map<String, String> event1Payload = Map.of("orderId", "1", "amount", "1000");
        OutboxEvent event1 = OutboxEvent.create("Order", "order-123", "test", "test", event1Payload);

        // When
        this.outboxEventMybatisCommandServiceImpl.save(event1);
        List<OutboxEvent> byStatusOrderByCreatedAtAsc = this.outboxEventMybatisReadServiceImpl.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        // then
        Assertions.assertFalse(byStatusOrderByCreatedAtAsc.isEmpty());
    }
}
