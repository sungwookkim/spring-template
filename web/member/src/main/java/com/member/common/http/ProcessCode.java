package com.member.common.http;

import lombok.Getter;

/**
 * <pre>
 *     Rest 응답코드를 관리하는 인터페이스
 *     분산될 여지가 있는 응답코드에 응집도를 높히기 위해 해당 인터페이스에서 구현.
 * </pre>
 */
public interface ProcessCode {
    String getCode();

    /**
     * <pre>
     *     Rest 전역 응답코드
     * </pre>
     */
    @Getter
    enum Common implements ProcessCode {
        SUCCESS("1000")
        , HTTP_STATUS_EXCEPTION("-9999")
        , APPLICATION_EXCEPTION("-10000")
        , UNSUPPORTED_FEATURE("-10001")
        ;

        private final String code;

        Common(String code) {
            this.code = code;
        }
    }
}
