package com.gri.template.impl.dao;

import com.gri.template.model.User;
import com.gri.template.model.enums.Status;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  UUID create(User user);

  boolean updateInfo(User user);

  boolean updateContacts(User user);

  boolean updatePassword(User user);

  Optional<User> find(UUID id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);

  Status delete(UUID id);
}
