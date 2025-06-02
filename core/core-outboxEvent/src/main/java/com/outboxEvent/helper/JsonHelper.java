package com.outboxEvent.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class JsonHelper {
    @Getter
    private final ObjectMapper objectMapper;

    public JsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@link JsonHelper}의 싱글톤 인스턴스를 제공합니다.
     *
     * 이 클래스는 애플리케이션 전체 생명주기 동안 공유되는 {@link JsonHelper}의 단일 인스턴스를 관리하도록 설계되었습니다.
     * 생성자는 {@link JsonHelper} 인스턴스를 전달받아 싱글톤을 초기화합니다.
     *
     * 싱글톤 패턴을 통해 {@link JsonHelper} 인스턴스에 중앙 집중식으로 접근할 수 있으며,
     * 이 인스턴스는 {@link ObjectMapper}와 같은 JSON 처리에 자주 사용되는 유틸리티들을 포함하고 있습니다.
     *
     * Spring의 {@code @Component} 어노테이션을 활용하여 Spring 컨테이너와 통합되며,
     * 이를 통해 자동 의존성 주입이 가능합니다.
     *
     * 애플리케이션 시작 시점에 싱글톤 인스턴스가 할당되고 이후에는 변경되지 않기 때문에
     * 스레드 안전성이 보장됩니다.
     */
    @Component
    public static class Singleton {
        @Getter
        private static JsonHelper instance;

        Singleton(JsonHelper jsonHelper) {
            instance = jsonHelper;
        }
    }
}
