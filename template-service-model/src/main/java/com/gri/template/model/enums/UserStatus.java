package com.gri.template.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum UserStatus {
//    CREATED(0),
    CREATED(10),
    REGISTERED(20),
    DELETED(90);

    private static final Map<Integer, UserStatus> LOOK_UP_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(UserStatus::code, Function.identity()));

    UserStatus(int code) {
        this.code = code;
    }

    private final int code;

    public int code() {
        return this.code;
    }

    public static UserStatus findByCode(Integer code) {
        return LOOK_UP_MAP.get(code);
    }
}

