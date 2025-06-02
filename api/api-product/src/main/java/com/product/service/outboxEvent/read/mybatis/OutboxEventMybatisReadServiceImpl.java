package com.product.service.outboxEvent.read.mybatis;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.config.transactional.annotaion.ProductReadTransactional;
import com.product.infra.outboxEvent.mybatis.OutboxEventMybatisRepository;
import com.product.service.outboxEvent.read.OutboxEventReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * OutboxEventMybatisReadServiceImpl 클래스는 {@code OutboxEventReadService} 인터페이스의 MyBatis 기반 구현체입니다.
 * 이 클래스는 Outbox 이벤트를 데이터베이스에서 조회하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@ProductReadTransactional
public class OutboxEventMybatisReadServiceImpl implements OutboxEventReadService {
    private final OutboxEventMybatisRepository outboxEventMybatisRepository;

    @Override
    public List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status) {
        return Optional.ofNullable(this.outboxEventMybatisRepository.findByStatusOrderByCreatedAtAsc(status))
                .filter(v -> !v.isEmpty())
                .orElse(List.of());
    }
}
