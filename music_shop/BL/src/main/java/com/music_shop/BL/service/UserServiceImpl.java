package com.music_shop.BL.service;

import com.music_shop.BL.API.UserService;
import com.music_shop.BL.exception.NonexistentCardException;
import com.music_shop.BL.exception.NonexistentCustomerException;
import com.music_shop.BL.exception.NonexistentEmployeeException;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.Card;
import com.music_shop.BL.model.User;
import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.API.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = new LoggerImpl(getClass().getName());

    private final UserRepo userRepo;
    private final CardRepo cardRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, CardRepo cardRepo) {
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
    }
    @Override
    public User getCustomerByLogin(String login) {
        log.info("getCustomerByLogin called with " + login);
        User user = userRepo.getUserByLogin(login);
        if (user == null) {
            RuntimeException e = new NonexistentCustomerException("Нет такого заказчика!");
            log.error("login failed", e);
            throw e;
        }
        Card card = cardRepo.getCardByCustomerLogin(login);
        if (card == null) {
            RuntimeException e = new NonexistentCardException("Карта не найдена!");
            log.error("login failed", e);
            throw e;
        }
        user.setCard(card);
        return user;
    }

    @Override
    public User getEmployeeByLogin(String login) {
        log.info("getCustomerByLogin called with " + login);
        User user = userRepo.getUserByLogin(login);
        if (user == null) {
            RuntimeException e = new NonexistentEmployeeException("Нет такого сотрудника!");
            log.error("login failed", e);
            throw e;
        }

        return user;
    }
}
