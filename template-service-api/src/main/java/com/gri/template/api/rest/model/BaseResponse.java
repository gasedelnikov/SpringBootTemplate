package com.gri.template.api.rest.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class BaseResponse {

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String status;
    private JsonNode data;

    public BaseResponse(String status, JsonNode data){
        this.status = status;
        this.data = data;
    }

    public BaseResponse(JsonNode data){
        this("OK", data);
    }

    public BaseResponse(Object data){
        this.status = "OK";
        this.data = objectMapper.valueToTree(data);
    }

}
