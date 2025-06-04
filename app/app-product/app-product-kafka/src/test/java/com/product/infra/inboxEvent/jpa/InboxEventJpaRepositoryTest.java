package com.product.infra.inboxEvent.jpa;

import com.inboxEvent.domain.entity.InboxEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"app-product-kafka-test"})
@SpringBootTest
public class InboxEventJpaRepositoryTest {
    @Autowired
    private InboxEventJpaRepository inboxEventJpaRepository;


    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        this.inboxEventJpaRepository.deleteAll(); // 각 테스트 전에 데이터 클린
    }

    @Test
    @DisplayName("InboxEvent 저장")
    void save() {
        // give
        InboxEvent inboxEvent = InboxEvent.newInstance("test", "test");

        // when
        this.inboxEventJpaRepository.save(inboxEvent);

        //then
        Assertions.assertNotNull(inboxEvent.getId());
    }
}
