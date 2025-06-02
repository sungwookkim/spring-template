package com.product.service.outboxEvent.read.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.config.transactional.annotaion.ProductReadTransactional;
import com.product.infra.outboxEvent.jpa.OutboxEventJpaRepository;
import com.product.service.outboxEvent.read.OutboxEventReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * OutboxEventJpaReadServiceImpl 클래스는 OutboxEventReadService 인터페이스의 구현체로,
 * OutboxEventJpaRepository를 통해 저장소에서 해당 조건에 맞는 Outbox 이벤트 데이터를 읽어오는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@ProductReadTransactional
public class OutboxEventJpaReadServiceImpl implements OutboxEventReadService {
    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Override
    public List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status) {
        return Optional.ofNullable(this.outboxEventJpaRepository.findByStatusOrderByCreatedAtAsc(status))
                .filter(v -> !v.isEmpty())
                .orElse(List.of());
    }
}
