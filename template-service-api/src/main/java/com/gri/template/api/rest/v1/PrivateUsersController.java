package com.gri.template.api.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.gri.template.api.rest.ResponseUtils;
import com.gri.template.api.rest.model.BaseResponse;
import com.gri.template.impl.cache.UserCacheService;
import com.gri.template.impl.exception.CustomValidationException;
import com.gri.template.impl.service.UserValidationService;
import com.gri.template.model.User;
import com.gri.template.model.error.CustomExceptionMessage;
import com.gri.template.security.UserAuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
final class PrivateUsersController {

    private final ObjectMapper objectMapper;
    private final UserAuthenticationService authenticationService;
    private final UserCacheService userCacheService;

    private final UserValidationService userValidationService;

    @Autowired
    public PrivateUsersController(@NotNull ObjectMapper objectMapper,
                                  @NotNull UserAuthenticationService authenticationService,
                                  @NotNull UserValidationService userValidationService,
                                  @NotNull UserCacheService userCacheService) {
        this.objectMapper = objectMapper;
        this.authenticationService = authenticationService;
        this.userValidationService = userValidationService;
        this.userCacheService = userCacheService;
    }

    @GetMapping("/current")
    public ResponseEntity<BaseResponse> getCurrent(@AuthenticationPrincipal final User user) {
        return Optional.ofNullable(user)
                .map(obj -> new BaseResponse(user))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update/info")
    public ResponseEntity<BaseResponse> updateInfo(@AuthenticationPrincipal final User user,
                                                   @RequestBody final User toUpdateUser) {
        if (!user.getId().equals(toUpdateUser.getId())) {
            throw new CustomValidationException(CustomExceptionMessage.FORBIDDEN_RESPONSE);
        }
        toUpdateUser.setEmail(user.getEmail());
        toUpdateUser.setPhoneNumber(user.getPhoneNumber());

        userCacheService.updateInfo(toUpdateUser);
        return userCacheService.find(user.getId())
                .map(userToGet -> new BaseResponse(userToGet))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update/contacts")
    public ResponseEntity<BaseResponse> updateContacts(@AuthenticationPrincipal final User user,
                                                       @RequestBody final User toUpdateUser) {
        if (!user.getId().equals(toUpdateUser.getId())) {
            throw new CustomValidationException(CustomExceptionMessage.FORBIDDEN_RESPONSE);
        }

        if (toUpdateUser.getPhoneNumber() != null) {
            if (!toUpdateUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                userValidationService.checkPhoneNumber(toUpdateUser.getPhoneNumber());
                authenticationService.logout(user);
            }
        }

        userCacheService.updateContacts(toUpdateUser);
        return userCacheService.find(user.getId())
                .map(userToGet -> new BaseResponse(userToGet))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update/password")
    public ResponseEntity<BaseResponse> updatePassword(@AuthenticationPrincipal final User user,
                                                       @RequestBody final JsonNode params) {
        String password = (params.has("password")) ? params.get("password").asText() : null;
        String idStr = (params.has("id")) ? params.get("id").asText() : null;

        if (password == null || "".equals(password) || idStr == null) {
            throw new CustomValidationException(CustomExceptionMessage.INVALID_DATA_RESPONSE);
        }
        UUID userId = UUID.fromString(idStr);
        if (!user.getId().equals(userId)) {
            throw new CustomValidationException(CustomExceptionMessage.FORBIDDEN_RESPONSE);
        }

        User toUpdateUser = User.builder()
                .id(userId)
                .password(authenticationService.encodePassword(password))
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();

        userCacheService.updatePassword(toUpdateUser);
        return userCacheService.find(user.getId())
                .map(userToGet -> new BaseResponse(userToGet))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@AuthenticationPrincipal final User user) {
        authenticationService.logout(user);
        return ResponseUtils.OK_RESPONSE;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUsrById(@AuthenticationPrincipal final User user,
                                                   @PathVariable @NotNull final UUID id) {
        User targetUser = userCacheService.find(id).orElse(null);
        if (targetUser != null) {
            return new ResponseEntity<>(new BaseResponse(targetUser), HttpStatus.OK);
        }
        else {
            throw new CustomValidationException(CustomExceptionMessage.USER_NOT_FOUND, ImmutableMap.of("id", id));
        }
    }

}
