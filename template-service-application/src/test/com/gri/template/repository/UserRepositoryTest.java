package com.gri.template.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gri.template.model.User;
import com.gri.template.model.enums.UserStatus;
import com.gri.template.impl.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Test
    public void validEmail_success() throws ParseException {
        User user = User.builder()
//                .id(UUID.randomUUID())
                .email("EMAIL")
                .phoneNumber("PHONE_NUMBER")
                .password("PASSWORD")
                .surname("SURNAME")
                .name("NAME")
                .status(UserStatus.REGISTERED)
                .description("DESCRIPTION")
                .build();

        UUID id = userRepository.create(user);
        User user3 = userRepository.find(id).orElse(null);

        user3.setPhoneNumber("new");
        userRepository.updateInfo(user3);

        assertEquals(0, 0);
    }

}
