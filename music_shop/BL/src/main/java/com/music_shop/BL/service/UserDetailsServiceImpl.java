package com.music_shop.BL.service;

import com.music_shop.BL.model.SecurityUser;
import com.music_shop.BL.model.User;
import com.music_shop.DB.API.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        System.out.println("load user " + login);
        User user = userRepository.getUserByLogin(login);
        return SecurityUser.fromUser(user);
    }
}
