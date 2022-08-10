package com.gri.template.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gri.template.api.rest.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final ResponseEntity<BaseResponse> OK_RESPONSE =
            getResponse("OK", null, HttpStatus.OK);

    public static final ResponseEntity<BaseResponse> REGISTER_OK_RESPONSE =
            getResponse("OK","Register is successes", HttpStatus.OK);

    public static ResponseEntity<BaseResponse> getResponse(String status, String info, HttpStatus httpStatus){
        return new ResponseEntity<>(new BaseResponse(status, objectMapper.createObjectNode().put("code", info)), httpStatus);
    }

}
