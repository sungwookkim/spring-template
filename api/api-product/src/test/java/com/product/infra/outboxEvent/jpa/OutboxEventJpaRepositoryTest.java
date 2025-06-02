package com.product.infra.outboxEvent.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

@ActiveProfiles(value = {"api-product-test"})
@SpringBootTest
public class OutboxEventJpaRepositoryTest {
    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        this.outboxEventJpaRepository.deleteAll(); // 각 테스트 전에 데이터 클린
    }

    @Test
    @DisplayName("OutboxEvent 저장 및 ID로 조회 테스트")
    void saveAndFindByIdTest() {
        // give
        Map<String, String> event1Payload = Map.of("orderId", "1", "amount", "1000");
        OutboxEvent event1 = OutboxEvent.create("Order", "order-123", "test", "test", event1Payload);

        // When
        OutboxEvent savedEvent = this.outboxEventJpaRepository.save(event1);
        Optional<OutboxEvent> foundEventOptional = this.outboxEventJpaRepository.findById(savedEvent.getId());

        // Then
        Assertions.assertNotNull(savedEvent.getId());
        Assertions.assertTrue(foundEventOptional.isPresent());

        OutboxEvent foundEvent = foundEventOptional.get();
        Assertions.assertEquals(foundEvent.getAggregateId(), event1.getAggregateId());
        Assertions.assertEquals(foundEvent.getEventType(), event1.getEventType());
        Assertions.assertEquals(OutboxEventStatus.PENDING, foundEvent.getStatus());
    }
}

