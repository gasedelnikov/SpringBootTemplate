package com.gri.template.impl.mapper;

import com.gri.template.model.User;
import com.gri.template.model.enums.UserStatus;
import com.gri.template.impl.DateUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;


/**
 * @author Gri
 */
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(UUID.fromString(rs.getString("ID")))
                .email(rs.getString("EMAIL"))
                .phoneNumber(rs.getString("PHONE_NUMBER"))
                .password(rs.getString("PASSWORD"))
                .surname(rs.getString("SURNAME"))
                .name(rs.getString("NAME"))
                .status(UserStatus.findByCode(rs.getInt("STATUS")))
                .enabled(true)
                .build();
    }
}
