package com.music_shop.DB.API;

import com.music_shop.BL.model.User;

public interface UserRepo {
    boolean addUser(User user);
    User getUserByLogin(String login);
}
