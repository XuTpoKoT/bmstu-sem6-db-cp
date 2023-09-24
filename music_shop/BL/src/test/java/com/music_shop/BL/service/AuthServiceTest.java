package com.music_shop.BL.service;

import com.music_shop.BL.exception.NonexistentLoginException;
import com.music_shop.BL.exception.OccupiedLoginException;
import com.music_shop.BL.exception.WeakPasswordException;
import com.music_shop.BL.exception.WrongPasswordException;
import com.music_shop.BL.model.User;
import com.music_shop.BL.API.AuthService;
import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.API.UserRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    static String login;
    static String password;
    static byte[] encodedPassword;
    static User user;

    @BeforeAll
    static void init() {
        login = "login1";
        password = "Qwe123";
        encodedPassword = Base64.getEncoder().encode(password.getBytes());
        user = User.builder().login(login).password(encodedPassword).build();
    }

    @Test
    public void givenValidLogin_whenLogin_thenSuccess() throws Exception {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(user);

        authService.login(login, password);
        verify(userRepo).getUserByLogin(login);
    }

    @Test()
    public void givenNonexistentLogin_whenLogin_thenThrowNonexistentLoginException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(null);

        assertThrows(NonexistentLoginException.class, () ->
                authService.login(login, password));
    }

    @Test
    public void givenWrongPassword_whenLogin_thenThrowWrongPasswordException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(user);

        assertThrows(WrongPasswordException.class, () ->
                authService.login(login, "Qwe"));
    }

    @Test
    public void givenValidCredentials_whenRegistration_thenSuccess() throws Exception {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(null);

        authService.registration(login, "Qwe123", User.Role.CUSTOMER, "Ivan", "Ivanov",
                "email");
        verify(userRepo).addUser(any(User.class));
    }

    @Test
    public void givenOccupiedLogin_whenRegistration_thenThrowOccupiedLoginException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(user);

        assertThrows(OccupiedLoginException.class, () ->
                authService.registration(login, "Qwe", User.Role.CUSTOMER, "Ivan", "Ivanov",
                        "email"));
    }

    @Test
    public void givenWeakPassword_whenRegistration_thenThrowWeakPasswordException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        AuthService authService = new AuthServiceImpl(userRepo, cardRepo);
        when(userRepo.getUserByLogin(login)).thenReturn(null);

        assertThrows(WeakPasswordException.class, () ->
                authService.registration(login, "Qwe", User.Role.CUSTOMER, "Ivan",
                        "Ivanov", "email"));
    }
}
