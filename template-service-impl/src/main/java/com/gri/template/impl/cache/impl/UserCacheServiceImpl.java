package com.gri.template.impl.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.gri.template.model.User;
import com.gri.template.model.enums.Status;
import com.gri.template.impl.cache.UserCacheService;
import com.gri.template.impl.dao.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class UserCacheServiceImpl implements UserCacheService {

    private final LoadingCache<String, Optional<User>> userByUsername;
    private final UserRepository userRepository;
    private final boolean isCacheEnabled;

    @Autowired
    public UserCacheServiceImpl(@Value("${cache.user.enabled:true}") boolean isCacheEnabled,
                                @Value("${jwt.clock-skew-sec:300}") int jwtClockSkewSec,
                                UserRepository userRepository){
        this.userRepository = userRepository;
        this.isCacheEnabled = isCacheEnabled;

        this.userByUsername = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofSeconds(jwtClockSkewSec))
                .build(new CacheLoader<>() {
                    @Override
                    public Optional<User> load(String username) {
                        return userRepository.findByUsername(username);
                    }
                });
    }

    @Override
    public void refresh(String username) {
        userByUsername.refresh(username);
    }

    @Override
    public UUID create(User user) {
        UUID result = userRepository.create(user);
        if (result != null) refresh(user);
        return result;
    }

    @Override
    public boolean updateInfo(User user) {
        boolean result = userRepository.updateInfo(user);
        if (result) refresh(user);
        return result;
    }

    @Override
    public boolean updateContacts(User user) {
        boolean result = userRepository.updateContacts(user);
        if (result) refresh(user);
        return result;
    }

    @Override
    public boolean updatePassword(User user) {
        boolean result = userRepository.updatePassword(user);
        if (result) refresh(user);
        return result;
    }

    @Override
    public Optional<User> find(UUID id) {
        return userRepository.find(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (!isCacheEnabled) {
            return userRepository.findByUsername(username);
        }
        try {
            return userByUsername.get(username);
        } catch (ExecutionException e) {
            log.warn("Failed to get User by username = " + username);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Status delete(UUID id) {
        return userRepository.find(id)
                .map(user -> {
                    Status status = userRepository.delete(id);
                    userByUsername.refresh(user.getUsername());
                    return status;
                })
                .orElse(null);
    }


    private void refresh(User user){
        userByUsername.refresh(user.getEmail());
        if (user.getPhoneNumber() != null) userByUsername.refresh(user.getPhoneNumber());
    }

}
