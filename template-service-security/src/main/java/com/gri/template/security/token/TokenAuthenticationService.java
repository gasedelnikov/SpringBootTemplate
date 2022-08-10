package com.gri.template.security.token;

import com.google.common.collect.ImmutableMap;
import com.gri.template.model.User;
import com.gri.template.security.UserAuthenticationService;
import com.gri.template.impl.cache.impl.UserCacheServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PACKAGE;

@Service
@AllArgsConstructor(access = PACKAGE)
@Primary
@Slf4j
final class TokenAuthenticationService implements UserAuthenticationService {
    @NonNull
    private final TokenService tokens;
    @NonNull
    private final UserCacheServiceImpl users;

    private static final String ID_FOR_ENCODE = "bcrypt";
    private static final Map<String, PasswordEncoder> encoders = new HashMap<>();
    static {
        encoders.put(ID_FOR_ENCODE, new BCryptPasswordEncoder());
//        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
//        encoders.put("sha256", new StandardPasswordEncoder());
    }
    private static final PasswordEncoder PASSWORD_ENCODER = new DelegatingPasswordEncoder(ID_FOR_ENCODE, encoders);

    @Override
    public String getToken(User user) {
        return tokens.expiring(ImmutableMap.of("username", user.getUsername()));
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return users.findByUsername(username);
    }

    @Override
    public boolean checkPassword(final User user, final String password) {
        return isPasswordMatches(password, user.getPassword());
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return Optional.of(tokens.verify(token))
                .map(map -> map.get("username"))
                .flatMap(users::findByUsername);
    }

    @Override
    public void logout(final User user) {
        user.setEnabled(false);
        log.debug("User logout: {}", user.getId());
    }

    @Override
    public String encodePassword(String password) {
        String encodedPassword = PASSWORD_ENCODER.encode(password);
        return encodedPassword;
    }

    @Override
    public boolean isPasswordMatches(String password, String encodedPassword) {
        boolean result = PASSWORD_ENCODER.matches(password, encodedPassword);
        log.debug("res = {}", result);
        return result;
    }
}
