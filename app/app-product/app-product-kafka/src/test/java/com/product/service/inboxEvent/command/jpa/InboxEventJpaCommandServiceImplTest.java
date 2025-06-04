package com.product.service.inboxEvent.command.jpa;

import com.inboxEvent.domain.entity.InboxEvent;
import com.product.helper.inbox.InboxHelper;
import com.product.infra.inboxEvent.jpa.InboxEventJpaRepository;
import com.product.service.inboxEvent.command.InboxEventCommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"app-product-kafka-test"})
@SpringBootTest
public class InboxEventJpaCommandServiceImplTest {
    @Autowired
    private InboxEventCommandService inboxEventJpaCommandServiceImpl;

    @Autowired
    private InboxHelper inboxHelper;

    @Test
    @DisplayName("InboxEvent 저장")
    void save() {
        // give
        InboxEvent inboxEvent = InboxEvent.newInstance("test", "test");

        // when
        boolean isReceived = this.inboxEventJpaCommandServiceImpl.isReceived(inboxEvent, "test");

        // then
        Assertions.assertTrue(isReceived);
    }

    @Test
    @DisplayName("InboxEvent 중복 저장")
    void duplicate_save() {
        // give & when
        this.inboxHelper.isReceived(InboxEvent.newInstance("test", "test"), "test");
        boolean isReceived = this.inboxHelper.isReceived(InboxEvent.newInstance("test", "test"), "test");

        // then
        Assertions.assertFalse(isReceived);
    }
}

