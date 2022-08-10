package com.gri.template.security;

import com.gri.template.model.User;

import java.util.Optional;

public interface UserAuthenticationService {

  String getToken(User user);

  Optional<User> findByUsername(String username);

  boolean checkPassword(User user,String password);

  Optional<User> findByToken(String token);

  void logout(User user);

  String encodePassword(String password);

  boolean isPasswordMatches(String password, String encodedPassword) ;
}
