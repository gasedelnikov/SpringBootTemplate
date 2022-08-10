package com.gri.template.api.rest.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@ToString(callSuper = true)
public class ErrorResponse {

    private String code;

    private String defaultMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> params;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private JsonNode data;

    public ErrorResponse(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
