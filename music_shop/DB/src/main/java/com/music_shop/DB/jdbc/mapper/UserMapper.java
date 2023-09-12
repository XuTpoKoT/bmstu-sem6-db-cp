package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements RowMapper<User> {

    @Override
    @Nullable
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        String login = rs.getString("login");
        byte[] password = rs.getBytes("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        Date birthDate = rs.getDate("birth_date");
        String birthDateStr  = null;
        if (birthDate != null) {
            birthDateStr = birthDate.toString();
        }
        String email = rs.getString("email");;
        String roleStr = rs.getString("role_");
        User.Role role = null;

        for (User.Role r : User.Role.values()) {
            if (roleStr.equals(r.name())) {
                role = r;
                break;
            }
        }

        if (role == null) { return null; }

        return User.builder().login(login).password(password).firstName(firstName).lastName(lastName)
                .birthDate(birthDateStr).email(email).role(role).build();
    }

}
