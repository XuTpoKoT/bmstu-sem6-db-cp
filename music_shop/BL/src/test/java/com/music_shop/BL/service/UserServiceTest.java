package com.music_shop.BL.service;

import com.music_shop.BL.API.UserService;
import com.music_shop.BL.exception.NonexistentCardException;
import com.music_shop.BL.exception.NonexistentCustomerException;
import com.music_shop.BL.exception.NonexistentEmployeeException;
import com.music_shop.BL.model.Card;
import com.music_shop.BL.model.User;
import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.API.UserRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    static User user;
    static private String login;

    @BeforeAll
    static void init() {
        login = "login1";
        String password = "Qwe123";
        byte[] encodedPassword = Base64.getEncoder().encode(password.getBytes());
        user = User.builder().login(login).password(encodedPassword).build();
    }

    @Test
    public void givenValidLogin_whenGetEmployeeByLogin_thenSuccess() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        UserService userService = new UserServiceImpl(userRepo, cardRepo);

        when(userRepo.getUserByLogin(login)).thenReturn(user);

        assertEquals(user, userService.getEmployeeByLogin(login));
    }

    @Test
    public void givenNonexistentLogin_whenGetEmployeeByLogin_thenThrowNonexistentEmployeeException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        UserService userService = new UserServiceImpl(userRepo, cardRepo);

        when(userRepo.getUserByLogin(login)).thenReturn(null);

        assertThrows(NonexistentEmployeeException.class, () -> {
            userService.getEmployeeByLogin(login);
        });
    }

    @Test
    public void givenValidLogin_whenGetCustomerByLogin_thenSuccess() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        UserService userService = new UserServiceImpl(userRepo, cardRepo);

        Card card = new Card(login, 100);
        user.setCard(card);
        when(userRepo.getUserByLogin(login)).thenReturn(user);
        when(cardRepo.getCardByCustomerLogin(login)).thenReturn(card);

        assertEquals(user, userService.getCustomerByLogin(login));
    }

    @Test
    public void givenNonexistentLogin_whenGetCustomerByLogin_thenThrowNonexistentEmployeeException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        UserService userService = new UserServiceImpl(userRepo, cardRepo);

        when(userRepo.getUserByLogin(login)).thenReturn(null);
        Card card = new Card(login, 100);
        when(cardRepo.getCardByCustomerLogin(login)).thenReturn(card);

        assertThrows(NonexistentCustomerException.class, () -> {
            userService.getCustomerByLogin(login);
        });
    }

    @Test
    public void givenNonexistentCard_whenGetCustomerByLogin_thenThrowNonexistentCardException() {
        UserRepo userRepo = mock(UserRepo.class);
        CardRepo cardRepo = mock(CardRepo.class);
        UserService userService = new UserServiceImpl(userRepo, cardRepo);

        when(userRepo.getUserByLogin(login)).thenReturn(user);
        when(cardRepo.getCardByCustomerLogin(login)).thenReturn(null);

        assertThrows(NonexistentCardException.class, () -> {
            userService.getCustomerByLogin(login);
        });
    }
}
