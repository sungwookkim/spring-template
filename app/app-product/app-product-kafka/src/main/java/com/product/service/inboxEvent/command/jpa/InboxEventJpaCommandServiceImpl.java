package com.product.service.inboxEvent.command.jpa;

import com.inboxEvent.domain.entity.InboxEvent;
import com.product.config.transactional.annotaion.ProductAppWriteTransactional;
import com.product.infra.inboxEvent.jpa.InboxEventJpaRepository;
import com.product.service.inboxEvent.command.InboxEventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * InboxEventJpaCommandServiceImpl 클래스는 {@link InboxEventCommandService} 인터페이스의 구현체로,
 * 수신된 InboxEvent를 데이터베이스에 저장하고 이벤트의 멱등성 보장을 위한 로직을 처리합니다.
 *
 * 이 클래스는 {@link InboxEventJpaRepository}를 사용하여 이벤트 데이터를 지속적으로 관리하며,
 * 이벤트 저장 시 발생하는 예외를 처리하여 저장 성공 여부를 반환합니다.
 *
 * 주요 기능:
 * - InboxEvent 엔터티를 데이터베이스에 저장
 * - 이벤트 처리 결과를 boolean 값으로 반환
 */
@Service
@RequiredArgsConstructor
@ProductAppWriteTransactional
public class InboxEventJpaCommandServiceImpl implements InboxEventCommandService {
    private final InboxEventJpaRepository inboxEventJpaRepository;

    /**
     * 주어진 InboxEvent 객체와 해당 consumerId를 기반으로 데이터베이스에 이미 처리된 이벤트인지 확인하고,
     * 처리되지 않은 경우 이벤트를 저장합니다.
     *
     * @param inboxEvent 저장 또는 확인하려는 InboxEvent 객체
     * @param consumerId 이벤트를 처리하는 소비자 또는 핸들러의 식별자
     * @return 주어진 이벤트가 처음 처리되는 경우 true를 반환하며, 이미 처리된 경우 false를 반환
     */
    @Override
    public boolean isReceived(InboxEvent inboxEvent, String consumerId) {
        if(this.inboxEventJpaRepository.existsByAggregateIdAndConsumerId(inboxEvent.getAggregateId(), consumerId)) {
            return false;
        }

        this.inboxEventJpaRepository.save(inboxEvent);
        return true;
    }

}

