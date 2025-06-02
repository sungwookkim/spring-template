package com.product.relay;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.enums.OutboxEventStatus;
import com.product.relay.app.RelayProcess;
import com.product.service.outboxEvent.read.OutboxEventReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OutboxEventRelay는 Outbox 이벤트를 주기적으로 폴링하고, 적절한 RelayProcess를 통해 이벤트를 특정 대상 시스템으로 전송하는 역할을 수행합니다.
 * 구성된 relayProcesses 리스트에 포함된 각 RelayProcess의 구현체를 통해 이벤트 전송 가능 여부를 확인한 뒤,
 * 이벤트의 상태를 기반으로 전송 작업을 수행합니다.
 *
 * 이 클래스는 Spring의 {@code @Scheduled} 어노테이션을 사용하여 주기적으로 실행됩니다.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OutboxEventRelay {
    private final OutboxEventReadService outboxEventJpaReadServiceImpl;
    private final List<RelayProcess> relayProcesses;

    /**
     * PENDING 상태의 OutboxEvent를 조회하고, 적절한 RelayProcess를 통해 이벤트를 처리 및 전송하는 메서드입니다.
     *
     * 이 메서드는 구성된 relayProcesses 리스트를 순회하면서, 각 이벤트에 대해 적합한 대상 시스템으로의 전송 가능 여부를 확인합니다.
     * 전송이 성공한 경우 해당 RelayProcess를 통해 이벤트를 전송하고 다음 이벤트로 진행합니다.
     * 전송 가능한 프로세스를 찾지 못한 경우 로그에 해당 정보를 기록합니다.
     *
     * 이 메서드는 Spring의 {@code @Scheduled} 어노테이션에 의해 주기적으로 실행됩니다.
     * 실행 주기와 초기 지연 시간은 설정 파일에서 관리 가능합니다.
     *
     * 주요 동작:
     * 1. PENDING 상태인 이벤트를 생성 시간 기준 오름차순으로 조회.
     * 2. 각 이벤트에 대해 relayProcesses 리스트를 순회하며 전송 가능 여부 확인.
     * 3. 전송 가능한 프로세스를 찾으면 해당 이벤트 전송.
     * 4. 전송 프로세스를 찾지 못하면 로그 기록.
     */
    @Scheduled(fixedDelayString = "${outbox.poller.fixedDelay:10000}", initialDelayString = "${outbox.poller.initialDelay:5000}") // 10초마다 폴링 (설정값으로 관리 가능)
    public void pollAndPublishEvents() {
        log.info("pollAndPublishEvents start");
        List<OutboxEvent> byStatusOrderByCreatedAtAsc = this.outboxEventJpaReadServiceImpl.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);
        for (OutboxEvent outboxEvent : byStatusOrderByCreatedAtAsc) {
            boolean isSend = false;
            for (RelayProcess relayProcess : relayProcesses) {
                isSend = relayProcess.isSend(outboxEvent.getSourceSystem());
                if(isSend) {
                    relayProcess.send(outboxEvent);
                    break;
                }
            }

            if(!isSend) {
              log.info("not found source system : {}", outboxEvent.getSourceSystem());
            }
        }
    }
}
