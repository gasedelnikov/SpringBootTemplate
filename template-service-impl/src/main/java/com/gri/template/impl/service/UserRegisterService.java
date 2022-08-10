package com.gri.template.impl.service;

import com.gri.template.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRegisterService {

    Optional<User> register(String email, String phoneNumber, String encodedPassword);


}
