package com.music_shop.BL.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Card {
    private String customerLogin;
    private int bonuses;

    public void incBonuses(int value) {
        bonuses += value;
    }
}
