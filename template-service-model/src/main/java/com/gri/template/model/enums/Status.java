package com.gri.template.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Status {
    CREATED(0),
    ACTIVE(10),
    UNLISTED(20),
    DELETED(90);

    private static final Map<Integer, Status> LOOK_UP_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Status::code, Function.identity()));

    Status(int code) {
        this.code = code;
    }

    private final int code;

    public int code() {
        return this.code;
    }

    public static Status findByCode(Integer code) {
        return LOOK_UP_MAP.get(code);
    }
}

