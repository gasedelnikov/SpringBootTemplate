package com.gri.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gri.template.TestUtils;
import com.gri.template.api.rest.model.BaseResponse;
import com.gri.template.api.rest.v1.open.PublicUsersController;
import com.gri.template.impl.exception.CustomValidationException;
import com.gri.template.model.enums.UserStatus;
import com.gri.template.model.error.CustomExceptionMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PublicUsersControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String email = "wrong@heartapart.ru";
    private static final String phoneNumber = "112233";

    @Autowired
    PublicUsersController publicUsersController;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Before
    public void beforeEach(){
        TestUtils.clearTables(namedJdbcTemplate, "USR");
    }

    @After
    public void afterEach() {
        TestUtils.clearTables(namedJdbcTemplate, "USR");
    }


    @Test
    public void loginByPhoneNumber_success() {
        String username = UUID.randomUUID().toString();

        ObjectNode reg = objectMapper.createObjectNode().put("phoneNumber", phoneNumber);
        reg.put("password", username);
        reg.put("email", email);

        ObjectNode login = objectMapper.createObjectNode().put("username", phoneNumber);
        login.put("password", username);

        try {
            ResponseEntity<BaseResponse> loginResInvalid = publicUsersController.login(login);
        }
        catch (CustomValidationException e){
            assertEquals(CustomExceptionMessage.INVALID_LOGIN_DATA.code(), e.getErrorCode());
        }

        ResponseEntity<BaseResponse> registerRes = publicUsersController.register(reg);
        String userId = TestUtils.checkUser(registerRes,  UserStatus.CREATED);

        ResponseEntity<BaseResponse> loginResByPhoneNumber = publicUsersController.login(login);
        assertEquals(userId, TestUtils.checkUser(loginResByPhoneNumber, UserStatus.CREATED));
    }

    @Test
    public void loginByEmail_success() {
        String username = UUID.randomUUID().toString();

        ObjectNode reg = objectMapper.createObjectNode();
        reg.put("password", username);
        reg.put("email", email);

        ObjectNode login = objectMapper.createObjectNode().put("username", email);
        login.put("password", username);

        try {
            ResponseEntity<BaseResponse> loginResInvalid = publicUsersController.login(login);
        }
        catch (CustomValidationException e){
            assertEquals(CustomExceptionMessage.INVALID_LOGIN_DATA.code(), e.getErrorCode());
        }

        ResponseEntity<BaseResponse> registerRes = publicUsersController.register(reg);
        String userId = TestUtils.checkUser(registerRes,  UserStatus.CREATED);

        ResponseEntity<BaseResponse> loginResByPhoneNumber = publicUsersController.login(login);
        assertEquals(userId, TestUtils.checkUser(loginResByPhoneNumber, UserStatus.CREATED));
    }

}
