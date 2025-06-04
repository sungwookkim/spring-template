package com.product.helper.inbox;

import com.inboxEvent.domain.entity.InboxEvent;
import com.product.service.inboxEvent.command.InboxEventCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * InboxHelper는 InboxEvent와 관련된 헬퍼 기능을 제공합니다.
 *
 * 이 클래스는 이벤트의 멱등성을 보장하기 위해
 * 주어진 InboxEvent가 이미 처리되었는지 여부를 확인하는 메서드를 포함합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboxHelper {
    private final InboxEventCommandService inboxEventJpaCommandServiceImpl;

    /**
     * 주어진 InboxEvent가 특정 소비자(consumer)에서 이미 처리되었는지 확인합니다.
     *
     * 이 메서드는 이벤트의 멱등성을 보장하기 위해 사용되며, 예외 처리 중 데이터베이스의
     * 중복 키 제약 조건 위반 여부를 검사하여 중복 처리를 방지합니다.
     *
     * @param inboxEvent 처리 여부를 확인할 InboxEvent 객체
     * @param consumerId 이벤트를 처리한 소비자의 식별자
     * @return 이벤트가 특정 소비자에 의해 이미 처리된 경우 true, 중복 처리된 경우 false
     * @throws DataIntegrityViolationException 중복이 아닌 다른 데이터 제약 조건 위반 또는 예외가 발생한 경우
     */
    public boolean isReceived(InboxEvent inboxEvent, String consumerId) {
        try {
            return this.inboxEventJpaCommandServiceImpl.isReceived(inboxEvent, consumerId);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e;

            while (cause.getCause() != null) {
                cause = cause.getCause();
            }

            String message = cause.getMessage();
            if (message != null
                    && (message.toLowerCase().contains("duplicate") || message.toLowerCase().contains("unique"))) {
                return false;
            } else {
                // 다른 제약 조건 위반 또는 예외
                throw e;
            }
        }
    }
}
