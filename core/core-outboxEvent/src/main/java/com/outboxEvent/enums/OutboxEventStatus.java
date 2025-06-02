package com.outboxEvent.enums;

import lombok.Getter;

@Getter
public enum OutboxEventStatus {
    PENDING,   // 발행 대기
    PUBLISHED, // 발행 완료
    FAILED     // 발행 실패
}
