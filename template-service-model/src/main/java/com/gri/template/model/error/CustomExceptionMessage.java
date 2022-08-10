package com.gri.template.model.error;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CustomExceptionMessage {

    FORBIDDEN_RESPONSE("S-0001", "Not enough rights"),

    INVALID_LOGIN_DATA("M-0001", "Invalid login data"),
    USER_PHONE_NUMBER_EXISTS_RESPONSE("M-0002", "User with this phoneNumber already exists"),
    EMAIL_FORMAT_ERR_RESPONSE("M-0003", "Email format error"),
    USER_EMAIL_EXISTS_RESPONSE("M-0004",  "User with this EMAIL already exists"),
    INVALID_DATA_RESPONSE("M-0005", "invalid data"),
    VERIFICATION_CODE_NOT_FOUND_RESPONSE("M-0006", "Verification code not found"),
    INVALID_REGISTER_DATA("M-0007", "Invalid email/phoneNumber and/or password"),
    UNKNOWN_STATUS("M-0008", "Unknown status"),


    SOME_BD_READ_ERR_RESPONSE("E-0001", "Some error on DB read"),
    SOME_UPDATE_ERR_RESPONSE("E-0002", "Some error on update"),
    REGISTER_ERR_RESPONSE("E-0003", "Register error"),
    PARSING_ERROR_NO_DATA_RESPONSE("E-0004",  "Profile not found"),
    EXTERNAL_CDN_SERVICE_ERR_RESPONSE("E-0005",  "CDN service error"),
    SOME_DATE_ERR_RESPONSE("E-0006", "Some data error"),

    NOT_FOUND("R-0001", "Not found"),
    USER_NOT_FOUND("R-0002", "User not found"),
    FILE_NOT_FOUND("R-0003", "File not found"),

    STATUS_ANY_OF("C-0021", "Status must be any of {anyOf}"),


    ;

    public static CustomExceptionMessage findByCode(String code) {
        return CODE_LOOK_UP_MAP.get(code);
    }

    private static final Map<String, CustomExceptionMessage> CODE_LOOK_UP_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(CustomExceptionMessage::code, Function.identity()));

    private final String code;
    private final String message;

    CustomExceptionMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String code() {
        return this.code;
    }
    public String message() {
        return this.message;
    }
}
