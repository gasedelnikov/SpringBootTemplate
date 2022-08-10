package com.gri.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gri.template.api.rest.model.BaseResponse;
import com.gri.template.model.enums.Status;
import com.gri.template.model.enums.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void checkApp(ResponseEntity<BaseResponse> getRes,
                                boolean isPublicInfoNotNull,
                                boolean isGuestInfoNotNull,
                                boolean isOwnerInfoNotNull,
                                Status status) {
        assertNotNull(getRes);
        checkApp(getRes.getBody().getData(), isPublicInfoNotNull, isGuestInfoNotNull, isOwnerInfoNotNull, status);
    }

    public static void checkApp(JsonNode jsonNode,
                                boolean isPublicInfoNotNull,
                                boolean isGuestInfoNotNull,
                                boolean isOwnerInfoNotNull,
                                Status status) {
        assertNotNull(jsonNode);
        assertEquals(isPublicInfoNotNull, jsonNode.hasNonNull("publicInfo"));
        assertEquals(isGuestInfoNotNull, jsonNode.hasNonNull("guestInfo"));
        assertEquals(isOwnerInfoNotNull, jsonNode.hasNonNull("ownerInfo"));

        assertEquals(status.name(), jsonNode.get("status").asText());
    }

    public static void clearTables(NamedParameterJdbcTemplate namedJdbcTemplate, String ...tables){
        for (String table:tables){
            namedJdbcTemplate.update("DELETE FROM "+table+" WHERE 1 = 1", new HashMap<>());
        }
    }

    public static String checkUser(ResponseEntity<BaseResponse> res, UserStatus status) {
        assertNotNull(res);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        String userId = res.getBody().getData().get("user").get("id").asText();
        assertNotNull(userId);
        assertNotNull(res.getBody().getData().get("token"));
        assertEquals(status.name(), res.getBody().getData().get("user").get("status").asText());
        return userId;
    }

}
