package com.outboxEvent.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JsonHelper {
    @Getter
    private static volatile JsonHelper instance;
    private final ObjectMapper objectMapper;

    public JsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void setInstance() {
        instance = this;
    }
}
