package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.model.User;
import com.music_shop.DB.API.UserRepo;
import com.music_shop.DB.jdbc.mapper.IntMapper;
import com.music_shop.DB.jdbc.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "/scheme.sql")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepoTest {
    private static String SQL_COUNT_USERS = "SELECT count(*) FROM public.user";
    private static final String SQL_GET_USER_BY_LOGIN = """
        SELECT login, convert_from(password, 'UTF-8') as password, role_, first_name, last_name, email, birth_date
        FROM public.user
        WHERE login = :login 
    """;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IntMapper intMapper;

    @Test
    public void givenValidLogin_whenGetUserByLogin_thenSuccess() {
        String login = "Ivan";
        String password = "Ivan2pass";
        User.Role role = User.Role.EMPLOYEE;
        byte[] encodedPassword = Base64.getEncoder().encode(password.getBytes());
        User expectedUser = User.builder().login(login).role(role).password(encodedPassword).build();

        User user = userRepository.getUserByLogin("Ivan");

        assertEquals(expectedUser.getLogin(), user.getLogin());
        assertEquals(expectedUser.getRole(), user.getRole());
        String decodedPassword = new String(Base64.getDecoder().decode(user.getPassword()), StandardCharsets.UTF_8);
        assertEquals(password, decodedPassword);
    }
    @Test
    public void givenWrongLogin_whenGetUserByLogin_thenReturnNull() {
        User user = userRepository.getUserByLogin("sfdgsfg");

        assertEquals(null, user);
    }

    @Test
    public void givenValidUser_whenAddUser_thenSuccess() {
        String login = "John";
        String password = "John34pass";
        User.Role role = User.Role.CUSTOMER;
        byte[] encodedPassword = Base64.getEncoder().encode(password.getBytes());
        User expectedUser = User.builder().login(login).role(role).password(encodedPassword).build();
        MapSqlParameterSource params = new MapSqlParameterSource();
        Integer expectedCnt = jdbc.queryForObject(SQL_COUNT_USERS, params, intMapper) + 1;

        userRepository.addUser(expectedUser);
        MapSqlParameterSource getParams = new MapSqlParameterSource();
        getParams.addValue("login", login);
        User user = jdbc.queryForObject(SQL_GET_USER_BY_LOGIN, getParams, userMapper);
        String decodedPassword = new String(Base64.getDecoder().decode(user.getPassword()), StandardCharsets.UTF_8);
        assertEquals(expectedUser.getLogin(), user.getLogin());
        assertEquals(expectedUser.getRole(), user.getRole());
        assertEquals(password, decodedPassword);

        Integer cnt = jdbc.queryForObject(SQL_COUNT_USERS, params, intMapper);


        assertEquals(expectedCnt, cnt);
    }
}