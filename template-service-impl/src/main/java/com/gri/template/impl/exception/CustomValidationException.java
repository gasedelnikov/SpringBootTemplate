package com.gri.template.impl.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.gri.template.model.error.CustomExceptionMessage;

import java.util.Map;

public class CustomValidationException extends CustomException {
    private static final long serialVersionUID = 5065695885653142372L;

    public CustomValidationException(CustomExceptionMessage message) {
        super(message);
    }

    public CustomValidationException(CustomExceptionMessage message, Map<String, ?> params) {
        super(message, params);
    }

    public CustomValidationException(CustomExceptionMessage message, JsonNode data){
        super(message, data);
    }
}