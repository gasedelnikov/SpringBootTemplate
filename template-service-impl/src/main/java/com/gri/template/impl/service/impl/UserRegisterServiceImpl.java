package com.gri.template.impl.service.impl;

import com.gri.template.model.User;
import com.gri.template.model.error.CustomExceptionMessage;
import com.gri.template.impl.cache.UserCacheService;
import com.gri.template.impl.exception.CustomValidationException;
import com.gri.template.impl.service.UserRegisterService;
import com.gri.template.impl.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserCacheService users;
    private final UserValidationService userValidationService;

    @Autowired
    public UserRegisterServiceImpl(UserCacheService users,
                                   UserValidationService userValidationService) {
        this.users = users;
        this.userValidationService = userValidationService;
    }

    @Override
    @Transactional
    public Optional<User> register(String email, String phoneNumber, String encodedPassword) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        }

        if ("".equals(phoneNumber)) phoneNumber = null;

        userValidationService.checkEmail(email);
        userValidationService.checkPhoneNumber(phoneNumber);

        User user = User.builder().phoneNumber(phoneNumber).email(email).password(encodedPassword).build();

        UUID userId = users.create(user);
        if (userId != null) {
            return users.find(userId);
        }
        else{
            throw new CustomValidationException(CustomExceptionMessage.REGISTER_ERR_RESPONSE);
        }
    }

}
