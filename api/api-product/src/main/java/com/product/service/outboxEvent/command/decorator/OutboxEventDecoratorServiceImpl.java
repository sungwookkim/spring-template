package com.product.service.outboxEvent.command.decorator;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.config.transactional.annotaion.ProductWriteTransactional;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutboxEvent 저장 기능을 데코레이터 패턴으로 제공하는 구현 클래스입니다.
 *
 * 이 클래스는 두 가지 저장 전략(JPA 및 MyBatis)을 조합하여 사용합니다.
 * JPA를 통해 단일 {@code OutboxEvent} 엔티티를 저장하며,
 * MyBatis를 통해 복수 {@code OutboxEvent} 엔티티를 한번에 저장합니다.
 *
 * 이 클래스는 트랜잭션 처리를 위해 {@code ProductWriteTransactional} 어노테이션을 사용하고,
 * {@code OutboxEventCommandService} 인터페이스를 구현합니다.
 */
@Service
@RequiredArgsConstructor
@ProductWriteTransactional
public class OutboxEventDecoratorServiceImpl implements OutboxEventCommandService {
    private final OutboxEventCommandService outboxEventJpaCommandServiceImpl;
    private final OutboxEventCommandService outboxEventMybatisCommandServiceImpl;

    @Override
    public void save(OutboxEvent outboxEvent) {
        this.outboxEventJpaCommandServiceImpl.save(outboxEvent);
    }

    @Override
    public void save(List<OutboxEvent> outboxEvents) {
        this.outboxEventMybatisCommandServiceImpl.save(outboxEvents);
    }

    @Override
    public void updateStatus(List<OutboxEvent> outboxEvents, OutboxEventStatus outboxEventStatus) {
        this.outboxEventMybatisCommandServiceImpl.updateStatus(outboxEvents, outboxEventStatus);
    }
}
