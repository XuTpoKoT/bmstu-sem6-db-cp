package com.music_shop.BL.service;

import com.music_shop.BL.API.AuthService;
import com.music_shop.BL.exception.NonexistentLoginException;
import com.music_shop.BL.exception.OccupiedLoginException;
import com.music_shop.BL.exception.WeakPasswordException;
import com.music_shop.BL.exception.WrongPasswordException;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.User;
import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.API.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final UserRepo userRepo;
    private final CardRepo cardRepo;

    @Autowired
    public AuthServiceImpl(UserRepo userRepo, CardRepo cardRepo) {
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
    }

    @Override
    public User.Role login(String login, String password) throws RuntimeException {
        log.info("login called with " + login);
        System.out.println(password);
        User user = userRepo.getUserByLogin(login);
        if (user == null) {
            RuntimeException e = new NonexistentLoginException("Нет такого логина!");
            log.error("login failed", e);
            throw e;
        }

        String decodedPassword = new String(Base64.getDecoder().decode(user.getPassword()), StandardCharsets.UTF_8);
        if (!decodedPassword.equals(password)) {
            RuntimeException e = new WrongPasswordException("Неверный пароль!");
            log.error("login failed", e);
            throw e;
        }
        return user.getRole();
    }

    @Transactional
    @Override
    public void registration(String login, String password, User.Role role, String firstName, String lastName,
                             String email)
            throws RuntimeException {
        log.info("registration called with " + login);
        System.out.println(password);
        User user = userRepo.getUserByLogin(login);
        if (user != null) {
            RuntimeException e = new OccupiedLoginException("Логин уже занят!");
            log.error("login failed", e);
            throw e;
        }

        if (!isPasswordStrong(password)) {
            RuntimeException e = new WeakPasswordException("Слабый пароль!");
            log.error("login failed", e);
            throw e;
        }
        byte[] encodedPassword = Base64.getEncoder().encode(password.getBytes());
        userRepo.addUser(User.builder().login(login).password(encodedPassword).firstName(firstName).lastName(lastName).
                role(role).email(email).build());
        cardRepo.createCard(login);
    }

    private boolean isPasswordStrong(String password) {
        log.debug("isPasswordStrong called");
        return password.length() >= 5
                && password.matches("(?=.*[0-9]).*")
                && password.matches("(?=.*[a-z]).*")
                && password.matches("(?=.*[A-Z]).*");
    }
}
