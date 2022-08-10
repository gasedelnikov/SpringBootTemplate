package com.gri.template.api.rest.v1.open;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.gri.template.api.rest.model.BaseResponse;
import com.gri.template.impl.service.UserRegisterService;
import com.gri.template.model.User;
import com.gri.template.model.enums.AccessStatus;
import com.gri.template.model.error.CustomExceptionMessage;
import com.gri.template.security.UserAuthenticationService;
import com.gri.template.impl.cache.UserCacheService;
import com.gri.template.impl.exception.CustomValidationException;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/v1/public/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PublicUsersController {

    private final ObjectMapper objectMapper;
    private final UserAuthenticationService authenticationService;
    private final UserCacheService userCacheService;
    private final UserRegisterService userRegisterService;

    public PublicUsersController(ObjectMapper objectMapper,
                                 UserAuthenticationService authenticationService,
                                 UserCacheService userCacheService,
                                 UserRegisterService userRegisterService) {
        this.objectMapper = objectMapper;
        this.authenticationService = authenticationService;
        this.userCacheService = userCacheService;
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody final JsonNode params) {
        String phoneNumber = (params.has("phoneNumber"))? params.get("phoneNumber").asText() : null;
        String password = (params.has("password"))? params.get("password").asText() : null;
        String email = (params.has("email"))? params.get("email").asText() : null;
        if (password == null || email == null){
            throw new CustomValidationException(CustomExceptionMessage.INVALID_REGISTER_DATA);
        }

        String encodedPassword = authenticationService.encodePassword(password);
        return userRegisterService.register(email, phoneNumber, encodedPassword)
                .map(this::getResultObjectNode)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->  new CustomValidationException(CustomExceptionMessage.REGISTER_ERR_RESPONSE));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody final JsonNode params) {
        final String username = (params != null && params.has("username"))? params.get("username").asText() : null;
        final String password = (params != null && params.has("password"))? params.get("password").asText() : null;
        if (username == null || password == null){
            throw new CustomValidationException(CustomExceptionMessage.INVALID_LOGIN_DATA);
        }
        return authenticationService.findByUsername(username)
                .filter(user -> authenticationService.checkPassword(user, password))
                .map(user -> {
                    user.setEnabled(true);
                    user.setUsername(username);
                    return user;
                })
                .map(this::getResultObjectNode)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomValidationException(CustomExceptionMessage.INVALID_LOGIN_DATA));
    }



    private BaseResponse getResultObjectNode(User user){
        ObjectNode obj = objectMapper.createObjectNode();
        obj.put("token", authenticationService.getToken(user));
        obj.set("user", objectMapper.valueToTree(user));

        return new BaseResponse("OK", obj);
    }
}
