package com.gri.template.impl.service.impl;

import com.gri.template.impl.service.EmailValidatorService;
import com.gri.template.model.error.CustomExceptionMessage;
import com.gri.template.impl.cache.UserCacheService;
import com.gri.template.impl.exception.CustomValidationException;
import com.gri.template.impl.service.UserValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserValidationServiceImpl implements UserValidationService {

    private final UserCacheService users;
    private final EmailValidatorService emailValidatorService;


    public UserValidationServiceImpl(UserCacheService users,
                                     EmailValidatorService emailValidatorService) {
        this.users = users;
        this.emailValidatorService = emailValidatorService;
    }

    @Override
    public void checkPhoneNumber(String phoneNumber) { //todo spring validations
        if (phoneNumber != null && users.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new CustomValidationException(CustomExceptionMessage.USER_PHONE_NUMBER_EXISTS_RESPONSE);
        }
    }

    @Override
    public void checkEmail(String email) { //todo spring validations
        if (!emailValidatorService.isValid(email)) {
            throw new CustomValidationException(CustomExceptionMessage.EMAIL_FORMAT_ERR_RESPONSE);
        }
    }

}
