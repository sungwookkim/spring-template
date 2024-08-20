package com.member.common.exception;

import com.member.common.http.ProcessCode;
import com.member.spring.handler.RestExceptionHandler;
import lombok.Getter;

/**
 * <pre>
 *     프로세스 처리 전용으로 사용하는 예외
 *
 *     해당 예외를 발생 시키면 {@link RestExceptionHandler#processException(ProcessException)}에서 전역 처리 후 응답 값을 응답한다.
 * </pre>
 */
@Getter
public class ProcessException extends RuntimeException {
    private final String resultCode;
    private final Object processCode;

    public <T extends ProcessCode> ProcessException(T processCode) {

        super(processCode.getCode());

        this.processCode = processCode;
        this.resultCode = processCode.getCode();
    }

    public ProcessCode getProcessCode() {
        return (ProcessCode) processCode;
    }
}
