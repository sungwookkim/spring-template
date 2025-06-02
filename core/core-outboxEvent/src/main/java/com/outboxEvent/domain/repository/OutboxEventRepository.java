package com.outboxEvent.domain.repository;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;

import java.util.List;
import java.util.Optional;

/**
 * OutboxEventRepository는 OutboxEvent 엔티티에 대한 데이터 액세스 로직을 정의하는
 * 리포지토리 인터페이스입니다. 특정 상태 값을 기준으로 데이터를 조회하는 등의
 * 커스텀 메서드를 제공합니다.
 */
public interface OutboxEventRepository {

    /**
     * 주어진 상태 값에 따라 필터링된 {@code OutboxEvent} 엔티티 목록을 조회하며,
     * 생성 시간 기준으로 오름차순 정렬하여 반환합니다.
     *
     * @param status 조회할 Outbox 이벤트들의 상태 값
     * @return 주어진 상태와 일치하는 {@code OutboxEvent} 엔티티 목록.
     *         생성 시간 기준으로 오름차순 정렬됨
     */
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}
