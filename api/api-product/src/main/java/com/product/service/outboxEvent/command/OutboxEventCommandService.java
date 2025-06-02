package com.product.service.outboxEvent.command;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;

import java.util.List;

/**
 * OutboxEvent 엔티티에 대한 저장 기능을 제공하는 서비스 인터페이스입니다.
 *
 * 이 인터페이스는 Outbox 패턴을 기반으로 이벤트를 영속화하여
 * 비동기 메시징이나 이벤트 처리를 지원하는 데 사용됩니다.
 */
public interface OutboxEventCommandService {

    /**
     * 주어진 {@code OutboxEvent} 엔티티를 저장합니다.
     *
     * @param outboxEvent 저장할 {@code OutboxEvent} 엔티티
     */
    void save(OutboxEvent outboxEvent);

    /**
     * 주어진 {@code OutboxEvent} 엔티티의 리스트를 저장합니다.
     *
     * @param outboxEvents 저장할 {@code OutboxEvent} 엔티티 리스트
     */
    void save(List<OutboxEvent> outboxEvents);

    void updateStatus(List<OutboxEvent> outboxEvents, OutboxEventStatus outboxEventStatus);
}
