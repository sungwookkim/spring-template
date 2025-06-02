package com.product.kafka.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PayloadHelper 클래스는 JSON 형식의 문자열 데이터를 제공된 클래스 타입으로 변환하는
 * 유틸리티 메서드를 제공합니다. 주로 데이터의 직렬화 및 역직렬화 과정에서 활용됩니다.
 *
 * 주요 기능:
 * - JSON 문자열을 특정 클래스 타입의 객체로 변환.
 *
 * 내부적으로 ObjectMapper를 사용하여 JSON 데이터를 처리하며, Jackson 라이브러리에
 * 의존성을 가집니다.
 *
 * 예외 처리:
 * - 변환 과정에서 JsonProcessingException이 발생할 경우 IllegalArgumentException으로 래핑하여 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class PayloadHelper {
    private final ObjectMapper objectMapper;

    /**
     * JSON 형식의 문자열 데이터를 주어진 클래스 타입의 객체로 변환합니다.
     *
     * @param payload 변환할 JSON 형식의 문자열
     * @param payloadClass 변환할 대상 클래스 타입
     * @param <T> 변환 대상 클래스의 타입
     * @return 변환된 클래스 타입 객체
     * @throws IllegalArgumentException JSON 데이터를 변환하는 과정에서 오류가 발생한 경우
     */
    public <T> T parser(String payload, Class<T> payloadClass) {
        try {
            String fixed = this.objectMapper.readValue(payload, String.class);
            return this.objectMapper.readValue(fixed, payloadClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
