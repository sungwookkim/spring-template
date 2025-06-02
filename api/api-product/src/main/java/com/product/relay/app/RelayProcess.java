package com.product.relay.app;

import com.outboxEvent.domain.entity.OutboxEvent;

/**
 * RelayProcess 인터페이스는 메시지 전송과 관련된 역할을 정의한다.
 * 구현체는 특정 시스템으로 메시지를 전송하는 방식과 해당 메시지의 전송 여부를 확인하는 로직을 포함해야 한다.
 */
public interface RelayProcess {
    /**
     * 특정 시스템으로 메시지가 전송되었는지 확인한다.
     *
     * @param sourceSystem 메시지를 전송하려는 대상 시스템의 이름
     * @return 메시지가 해당 시스템으로 전송된 경우 true, 그렇지 않으면 false
     */
    boolean isSend(String sourceSystem);

    /**
     * 주어진 OutboxEvent 객체를 처리하여 메시지를 전송합니다.
     *
     * @param outboxEvent 전송하려는 메시지 정보를 담고 있는 OutboxEvent 객체
     */
    void send(OutboxEvent outboxEvent);
}
