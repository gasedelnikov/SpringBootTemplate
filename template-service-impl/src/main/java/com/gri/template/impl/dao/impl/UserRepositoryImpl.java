package com.gri.template.impl.dao.impl;

import com.gri.template.model.User;
import com.gri.template.model.enums.Status;
import com.gri.template.model.enums.UserStatus;
import com.gri.template.impl.DateUtils;
import com.gri.template.impl.dao.UserRepository;
import com.gri.template.impl.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final static String TABLE = "USR";
    private final static UserMapper USER_MAPPER = new UserMapper();

    private final static List<String> FIELDS_LIST = List.of("ID", "EMAIL", "PHONE_NUMBER", "NAME", "SURNAME", "PASSWORD",
            "STATUS", "DESCRIPTION");
    private final static String FIELDS = String.join(", ", FIELDS_LIST);

    private final static List<String> FIELDS_UPDATE_INFO = List.of("NAME", "SURNAME", "DESCRIPTION");
    private final static String STR_UPDATE_INFO = FIELDS_UPDATE_INFO.stream()
            .map(f -> f + " = :" + f).collect(Collectors.joining(", "));

    private final static List<String> FIELDS_UPDATE_CONTACTS = List.of("EMAIL", "PHONE_NUMBER");
    private final static String STR_UPDATE_CONTACTS = FIELDS_UPDATE_CONTACTS.stream()
            .map(f -> f + " = :" + f).collect(Collectors.joining(", "));

    private final static String SQL_INSERT =
            "INSERT INTO " + TABLE + " (ID, EMAIL, PHONE_NUMBER, PASSWORD, STATUS)" +
           " SELECT :ID, :EMAIL, :PHONE_NUMBER, :PASSWORD, :STATUS"  +
            " WHERE NOT EXISTS (SELECT null FROM " + TABLE + " B WHERE B.ID = :ID)";

    private final static String SQL_UPDATE_INFO =
            "UPDATE " + TABLE +
              " SET " + STR_UPDATE_INFO +
            " WHERE ID = :ID";


    private final static String SQL_UPDATE_PASSWORD =
            "UPDATE " + TABLE +
              " SET PASSWORD = :PASSWORD"  +
            " WHERE ID = :ID";

    private final static String SQL_UPDATE_CONTACTS =
            "UPDATE " + TABLE +
              " SET " + STR_UPDATE_CONTACTS +
            " WHERE ID = :ID";

    private final static String SQL_UPDATE_STATUS =
            "UPDATE " + TABLE +
              " SET STATUS = :STATUS" +
            " WHERE ID = :ID";

    private final static String SQL_FIND_BY_ID =
            "SELECT " + FIELDS +
             " FROM " + TABLE +
            " WHERE ID = :ID" +
              " AND STATUS <> " + Status.DELETED.code();

    private final static String SQL_FIND_BY_USERNAME =
            "SELECT " + FIELDS +
             " FROM " + TABLE +
            " WHERE (EMAIL = :EMAIL" +
                    " OR PHONE_NUMBER = :PHONE_NUMBER" +
                    " OR PHONE_NUMBER = overlay(:PHONE_NUMBER placing '7' from 1 for 1))" +
              " AND STATUS <> " + Status.DELETED.code() +
            " LIMIT 1";

    private final static String SQL_FIND_BY_EMAIL =
            "SELECT " + FIELDS +
             " FROM " + TABLE +
            " WHERE EMAIL = :EMAIL" +
              " AND STATUS <> " + Status.DELETED.code() +
            " LIMIT 1";

    private final static String SQL_FIND_BY_PHONE_NUMBER =
            "SELECT " + FIELDS +
             " FROM " + TABLE +
            " WHERE PHONE_NUMBER = :PHONE_NUMBER" +
              " AND STATUS <> " + Status.DELETED.code() +
            " LIMIT 1";

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public UUID create(final User user) {
        user.setStatus(UserStatus.CREATED);
        user.setId(UUID.randomUUID());
        if (updatePrepare(SQL_INSERT, user)){
            return user.getId();
        }
        else{
            return null;
        }
    }

    @Override
    public boolean updateInfo(final User user) {
        return updatePrepare(SQL_UPDATE_INFO, user);
    }

    @Override
    public boolean updateContacts(User user){
        return updatePrepare(SQL_UPDATE_CONTACTS, user);
    }

    @Override
    public boolean updatePassword(User user){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ID", user.getId());
        parameters.put("PASSWORD", user.getPassword());
        return updatePrepare(SQL_UPDATE_PASSWORD, parameters);
    }
    @Override
    public Optional<User> find(final UUID id) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID", id);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(SQL_FIND_BY_ID, parameters, USER_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            log.debug("find: not found for: {}", id);
        } catch (DataAccessException e) {
            log.error("find: query error: id = {}; Message = {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            String phoneNumber = username.replaceAll("[^0-9]", "");

            parameters.put("EMAIL", username);
            parameters.put("PHONE_NUMBER", phoneNumber);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(SQL_FIND_BY_USERNAME, parameters, USER_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            log.debug("findByUsername: not found for: {}", username);
        } catch (DataAccessException e) {
            log.error("findByUsername: query error: username = {}; Message = {}", username, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("EMAIL", email);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, parameters, USER_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            log.debug("findByEmail: not found for: {}", email);
        } catch (DataAccessException e) {
            log.error("findByEmail: query error: email = {}; Message = {}", email, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PHONE_NUMBER", phoneNumber);
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(SQL_FIND_BY_PHONE_NUMBER, parameters, USER_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            log.debug("findByPhoneNumber: not found for: {}", phoneNumber);
        } catch (DataAccessException e) {
            log.error("findByPhoneNumber: query error: phoneNumber = {}; Message = {}", phoneNumber, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Status delete(UUID id) {
        if (id == null) {
            return null;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ID", id);
        parameters.put("STATUS", Status.DELETED.code());

        return updatePrepare(SQL_UPDATE_STATUS, parameters) ? Status.DELETED : null;
    }

    private boolean updatePrepare(String sqlText, User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ID", user.getId());
        parameters.put("EMAIL", user.getEmail());
        parameters.put("PHONE_NUMBER", user.getPhoneNumber());
        parameters.put("NAME", user.getName());
        parameters.put("SURNAME", user.getSurname());
        parameters.put("PASSWORD", user.getPassword());
        if (user.getStatus() != null) parameters.put("STATUS", user.getStatus().code());
        parameters.put("DESCRIPTION", user.getDescription());
        return updatePrepare(sqlText, parameters);
    }

    private boolean updatePrepare(String sqlText, Map<String, Object> parameters) {
        try {
            namedJdbcTemplate.update(sqlText, parameters);
            return true;
        } catch (DataAccessException e) {
            log.error("updatePrepare: query error: sqlText = {}", sqlText);
            return false;
        }
    }


}
