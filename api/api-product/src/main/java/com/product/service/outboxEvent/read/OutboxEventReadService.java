package com.product.service.outboxEvent.read;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;

import java.util.List;

/**
 * OutboxEventReadService는 Outbox 이벤트를 읽어오기 위한 서비스 인터페이스를 정의합니다.
 * 구현체를 통해 특정 조건에 따라 Outbox 이벤트 데이터를 검색할 수 있습니다.
 */
public interface OutboxEventReadService {
    /**
     * 주어진 상태를 기반으로 모든 {@code OutboxEvent} 목록을 생성일자 오름차순으로 정렬하여 반환합니다.
     *
     * @param status 검색할 이벤트의 상태. 예: {@code OutboxEventStatus.PENDING}
     * @return 상태가 주어진 조건에 부합하는 {@code OutboxEvent} 목록
     */
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}
