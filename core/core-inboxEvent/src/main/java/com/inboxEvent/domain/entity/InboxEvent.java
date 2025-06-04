package com.inboxEvent.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * InboxEvent 클래스는 멱등성을 보장하기 위해 수신된 메시지에 대한 정보를 저장하는 엔터티입니다.
 * 이 클래스는 Kafka 등 메시징 시스템에서 수신된 이벤트의 고유 식별자와 메시지를 처리한 소비자 정보를 저장하며,
 * 이벤트가 중복 처리되지 않도록 설계되었습니다.
 *
 * 주요 특징:
 * - 수신된 메시지의 고유 식별자를 기반으로 멱등성 보장
 * - 소비자 ID와 수신 시간 정보를 함께 저장
 * - equals 및 hashCode 메서드는 aggregateId를 기준으로 구현
 *
 * 주요 필드:
 * - id: 데이터베이스의 기본 키로 사용되는 자동 생성 값
 * - aggregateId: 메시지의 고유 식별자 (예: Kafka 키 또는 이벤트 ID)
 * - consumerId: 이벤트를 처리한 소비자 또는 핸들러의 식별자
 * - receivedAt: 이벤트가 수신된 시간
 *
 * 주요 메서드:
 * - newInstance: 주어진 aggregateId와 consumerId를 사용하여 새로운 InboxEvent 객체를 생성
 * - equals: aggregateId를 기준으로 객체 동등성 비교
 * - hashCode: aggregateId를 기반으로 해시 코드 생성
 */
@Entity
@Table(name = "inbox_event",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aggregate_id", "consumer_id"})
    }
)
@Getter
@NoArgsConstructor
public class InboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId; // 수신된 메시지의 고유 ID (예: Kafka 메시지 키 또는 헤더의 이벤트 ID)

    @Column(name = "consumer_id", nullable = false)
    private String consumerId; // 이 메시지를 처리하는 컨슈머/핸들러의 식별자

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();

    /**
     * 주어진 aggregateId와 consumerId를 사용하여 새로운 {@code InboxEvent} 객체를 생성합니다.
     *
     * @param aggregateId 수신된 메시지의 고유 식별자
     * @param consumerId 메시지를 처리하는 소비자 또는 핸들러의 식별자
     * @return 새로 생성된 {@code InboxEvent} 객체
     */
    public static InboxEvent newInstance(String aggregateId, String consumerId) {
        InboxEvent inboxEvent = new InboxEvent();
        inboxEvent.aggregateId = aggregateId;
        inboxEvent.consumerId = consumerId;

        return inboxEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InboxEvent that = (InboxEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(consumerId, that.consumerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, consumerId);
    }
}
