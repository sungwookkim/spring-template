package com.product.service.inboxEvent.command;

import com.inboxEvent.domain.entity.InboxEvent;

/**
 * InboxEventCommandService는 이벤트의 수신 여부를 처리하는 서비스 인터페이스입니다.
 *
 * 이 인터페이스는 InboxEvent를 기반으로 이벤트의 멱등성을 보장합니다.
 * 구현체는 데이터베이스 저장 또는 기타 비즈니스 로직을 통해
 * 특정 이벤트가 이미 처리되었는지 여부를 확인할 수 있습니다.
 */
public interface InboxEventCommandService {

    /**
     * 주어진 InboxEvent가 특정 소비자(consumer)에 의해 이미 처리되었는지 확인합니다.
     *
     * @param inboxEvent 처리 여부를 확인할 InboxEvent 객체
     * @param consumerId 이벤트를 처리한 소비자의 식별자
     * @return 이벤트가 특정 소비자에 의해 이미 처리된 경우 true, 그렇지 않으면 false
     */
    boolean isReceived(InboxEvent inboxEvent, String consumerId);
}
