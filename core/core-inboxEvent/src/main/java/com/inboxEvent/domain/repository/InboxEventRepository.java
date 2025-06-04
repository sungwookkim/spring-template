package com.inboxEvent.domain.repository;

/**
 * InboxEvent 엔터티에 대한 사용자 정의 데이터 접근 로직을 정의하는 레포지토리 인터페이스.
 *
 * 이 인터페이스는 InboxEvent 엔터티와 관련된 비즈니스 로직을 구현하기 위한 메서드를 제공합니다.
 * 특히 멱등성을 보장하기 위해 aggregateId와 consumerId를 기준으로 엔터티의 존재 여부를 확인하는
 * 메서드를 포함합니다.
 */
public interface InboxEventRepository {

    /**
     * 주어진 aggregateId와 consumerId를 기준으로 데이터베이스에 해당 엔터티가 존재하는지 확인합니다.
     *
     * @param aggregateId 확인할 대상의 고유 식별자
     * @param consumerId 이벤트를 처리하는 소비자 또는 핸들러의 식별자
     * @return 데이터베이스에 주어진 조건이 일치하는 엔터티가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByAggregateIdAndConsumerId(String aggregateId, String consumerId);
}
