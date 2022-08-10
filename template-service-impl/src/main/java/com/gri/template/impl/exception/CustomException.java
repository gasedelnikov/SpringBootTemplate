package com.gri.template.impl.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.gri.template.model.error.CustomExceptionMessage;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = -6287769897594499380L;

    private final String errorCode;
    private Map<String, String> params;
    private JsonNode data;

    private CustomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    private CustomException(String errorCode, String message, Map<String, ?> params, JsonNode data) {
        this(errorCode, message);
        this.params = (params == null)? null : convertParamsMap(params);
        this.data = data;
    }

    public CustomException(CustomExceptionMessage message, JsonNode data) {
        this(message.code(), message.message(), null, data);
    }

    public CustomException(CustomExceptionMessage message) {
        this(message.code(), message.message());
    }

    public CustomException(CustomExceptionMessage message, Map<String, ?> params) {
        this(message.code(), message.message(), params, null);
    }

    private Map<String, String> convertParamsMap(Map<String, ?> params) {
        return params.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            if (entry.getValue() instanceof Collection) {
                                return ((Collection<?>) entry.getValue()).stream()
                                        .map(Object::toString)
                                        .collect(Collectors.joining(","));
                            }
                            else return entry.getValue().toString();

                        }));
    }
}