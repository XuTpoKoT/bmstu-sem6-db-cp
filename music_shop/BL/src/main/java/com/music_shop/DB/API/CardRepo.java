package com.music_shop.DB.API;

import com.music_shop.BL.model.Card;

public interface CardRepo {
    Card getCardByCustomerLogin(String customerLogin);
    void createCard(String customerLogin);
    void updateCard(Card card);
}
