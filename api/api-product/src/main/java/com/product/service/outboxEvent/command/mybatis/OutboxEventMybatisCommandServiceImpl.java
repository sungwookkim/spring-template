package com.product.service.outboxEvent.command.mybatis;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.config.transactional.annotaion.ProductWriteTransactional;
import com.product.infra.outboxEvent.mybatis.OutboxEventMybatisRepository;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MyBatis 기반의 Outbox 이벤트 처리 기능을 제공하는 서비스 구현 클래스입니다.
 * {@link OutboxEventCommandService}를 구현하며, Outbox 이벤트 저장 작업 등을 담당합니다.
 *
 * 이 클래스는 MyBatis 리포지토리인 {@link OutboxEventMybatisRepository}를 사용하여
 * 데이터베이스와 상호작용하며, 특정 트랜잭션 정책이 적용된 상태에서 동작합니다.
 *
 * 애플리케이션의 Outbox 패턴을 활용하여 이벤트 기반 비동기 처리를 수행할 수 있도록 지원합니다.
 */
@Service
@RequiredArgsConstructor
@ProductWriteTransactional
public class OutboxEventMybatisCommandServiceImpl implements OutboxEventCommandService {
    private final ApplicationContext applicationContext;

    private final OutboxEventMybatisRepository outboxEventMybatisRepository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        applicationContext.getBean(OutboxEventMybatisCommandServiceImpl.class).save(List.of(outboxEvent));
    }

    @Override
    public void save(List<OutboxEvent> outboxEvents) {
        this.outboxEventMybatisRepository.saveAll(outboxEvents);
    }

    @Override
    public void updateStatus(List<OutboxEvent> outboxEvents, OutboxEventStatus outboxEventStatus) {
        for(OutboxEvent outboxEvent : outboxEvents) {
            outboxEvent.nowUpdateAt();
        }

        this.outboxEventMybatisRepository.updateStatus(outboxEvents, outboxEventStatus);
    }
}
