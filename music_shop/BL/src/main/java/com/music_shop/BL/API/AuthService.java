package com.music_shop.BL.API;

import com.music_shop.BL.model.User;

public interface AuthService {
    User.Role login(String login, String password) throws Exception;
    void registration(String login, String password) throws Exception;
}
