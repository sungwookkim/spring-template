package com.member.spring.config.handler;

import com.member.common.exception.ProcessException;
import com.member.common.http.ProcessCode;
import com.member.common.http.RestResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * <pre>
     *     프로세스 처리 예외인 {@link ProcessException} 예외에 대한 응답 값이 설정하여 응답 반환.
     * </pre>
     */
    @ExceptionHandler(ProcessException.class)
    public ResponseEntity<RestResponseResult<Object>> processException(ProcessException processException) {
        logger.error("Stack trace:", processException);

        return ResponseEntity.ok(new RestResponseResult<>(processException.getProcessCode()
                , ""
                , HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponseResult<Object>> processException(Exception exception) {
        logger.error("Stack trace:", exception);

        return ResponseEntity.ok(new RestResponseResult<>(ProcessCode.Common.APPLICATION_EXCEPTION
                , ""
                , HttpStatus.SERVICE_UNAVAILABLE));
    }

    /**
     * <pre>
     *     예외에 따른 응답 값을 공통으로 처리하는 부분을 현 프로젝트에 맞게 끔 반환하게 하기 위해 오버라이드.
     * </pre>
     *
     * @param ex the exception to handle
     * @param body the body to use for the response
     * @param headers the headers to use for the response
     * @param statusCode the status code to use for the response
     * @param request the current request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex
            , Object body
            , HttpHeaders headers
            , HttpStatusCode statusCode
            , WebRequest request) {

        logger.error("Stack trace:", ex);

        return ResponseEntity.ok(new RestResponseResult<>(ProcessCode.Common.HTTP_STATUS_EXCEPTION
                , ""
                , Objects.requireNonNull(HttpStatus.resolve(statusCode.value()))));
    }
}

