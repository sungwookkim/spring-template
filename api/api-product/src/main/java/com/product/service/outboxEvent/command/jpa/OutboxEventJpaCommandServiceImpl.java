package com.product.service.outboxEvent.command.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.domain.repository.OutboxEventRepository;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.config.transactional.annotaion.ProductWriteTransactional;
import com.product.infra.outboxEvent.jpa.OutboxEventJpaRepository;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutboxEventJpaCommandServiceImpl 클래스는 OutboxEventCommandService 인터페이스를 구현하며,
 * OutboxEvent 엔티티에 대한 저장 기능을 제공합니다.
 *
 * 이 클래스는 JPA를 사용하여 단일 OutboxEvent 엔티티를 데이터베이스에 저장하거나,
 * MyBatis를 사용하여 OutboxEvent 엔티티의 리스트를 저장하는 기능을 수행합니다.
 *
 * {@code @Service} 애너테이션을 사용하여 Spring 서비스 컴포넌트로 정의되어 있으며,
 * {@code @ProductWriteTransactional} 애너테이션을 통해 트랜잭션 관리를 처리합니다.
 */
@Service
@RequiredArgsConstructor
@ProductWriteTransactional
public class OutboxEventJpaCommandServiceImpl implements OutboxEventCommandService {
    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        this.outboxEventJpaRepository.save(outboxEvent);
    }

    @Override
    public void save(List<OutboxEvent> outboxEvents) {
        throw new IllegalCallerException("지원하지 않는 기능 입니다.");
    }

    @Override
    public void updateStatus(List<OutboxEvent> outboxEvents, OutboxEventStatus outboxEventStatus) {
        for(OutboxEvent outboxEvent : outboxEvents) {
            outboxEvent.setStatus(outboxEventStatus);
            outboxEvent.nowUpdateAt();
        }

        this.outboxEventJpaRepository.saveAll(outboxEvents);
    }
}
