package com.payload;

/**
 * 제품 생성 이벤트의 페이로드를 나타내는 클래스.
 *
 * 이 클래스는 특정 카테고리에 속한 제품의 생성과 관련된 정보를 포함합니다.
 * 이를 통해 생성된 제품과 해당 카테고리를 명확히 식별할 수 있습니다.
 *
 * 이 클래스는 이벤트 기반 아키텍처에서 OutboxEvent와 함께 사용되어
 * 카테고리와 제품 생성 정보가 담긴 이벤트 데이터를 전송하는 데 활용됩니다.
 *
 * 주요 필드:
 * - categoryId: 제품이 속한 카테고리의 식별자
 * - productId: 생성된 제품의 식별자
 */
public record ProductCreatedEventPayload(Long categoryId, Long productId) {
}
