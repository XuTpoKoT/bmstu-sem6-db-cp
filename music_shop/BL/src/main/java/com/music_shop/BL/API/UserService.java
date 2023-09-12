package com.music_shop.BL.API;

import com.music_shop.BL.model.User;

public interface UserService {
    User getCustomerByLogin(String login);
    User getEmployeeByLogin(String login);
}
