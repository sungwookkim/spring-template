package com.outboxEvent.domain.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.outboxEvent.enums.OutboxEventStatus;
import com.outboxEvent.enums.OutboxEventType;
import com.outboxEvent.helper.JsonHelper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 데이터베이스 테이블 "outbox_event"에 매핑되는 엔티티 클래스입니다.
 * 이 클래스는 Outbox 패턴을 구현하기 위해 사용되며, 비동기 이벤트의 관리를 담당합니다.
 */
@Entity
@Table(name = "outbox_event")
@DynamicUpdate
@Getter
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType; // 예: "Product", "Stock" 등 이벤트가 발생한 주 엔티티 타입

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;   // 예: Product ID, Stock ID 등 해당 엔티티의 식별자

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private OutboxEventType eventType; // 예: 이벤트 유형

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;       // 이벤트 데이터를 담고 있는 JSON 문자열

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxEventStatus status = OutboxEventStatus.PENDING;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    @Column(name = "message_key", nullable = false)
    private String messageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void nowUpdateAt() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 지정된 애그리거트 타입, 애그리거트 ID, 페이로드에 대해 새로운 {@code OutboxEvent} 객체를 생성합니다.
     * 생성된 이벤트는 이벤트 타입이 {@code CREATE}로 설정되고, 상태는 {@code PENDING}으로 설정됩니다.
     *
     * @param <T> 페이로드의 타입
     * @param aggregateType 이벤트가 연관된 애그리거트 엔티티의 타입
     * @param aggregateId 이벤트가 연관된 애그리거트 엔티티의 식별자
     * @param payload 이벤트와 연관된 데이터로, JSON 문자열로 직렬화됩니다
     * @return 지정된 속성으로 구성된 새로운 {@code OutboxEvent} 인스턴스
     * @throws IllegalArgumentException 페이로드를 JSON 문자열로 직렬화할 수 없는 경우 발생
     */
    public static <T> OutboxEvent create(String aggregateType, String aggregateId, String sourceSystem, String messageKey, T payload) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.aggregateType = aggregateType;
        outboxEvent.aggregateId = aggregateId;
        outboxEvent.sourceSystem = sourceSystem;
        outboxEvent.messageKey = messageKey;
        outboxEvent.eventType = OutboxEventType.CREATE;
        outboxEvent.status = OutboxEventStatus.PENDING;

        try {
            outboxEvent.payload = JsonHelper.getInstance().getObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }

        return outboxEvent;
    }
}
