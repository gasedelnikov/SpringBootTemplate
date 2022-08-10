package com.gri.template.impl.exception;

import com.gri.template.model.error.CustomExceptionMessage;

import java.util.Map;

public class CustomRuntimeException extends CustomException {
    private static final long serialVersionUID = 5065695885653142372L;

    public CustomRuntimeException(CustomExceptionMessage message) {
        super(message);
    }

    public CustomRuntimeException(CustomExceptionMessage message, Map<String, ?> params) {
        super(message, params);
    }
}