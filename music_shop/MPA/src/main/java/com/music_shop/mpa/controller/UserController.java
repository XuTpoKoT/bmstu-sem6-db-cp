package com.music_shop.mpa.controller;

import com.music_shop.BL.API.UserService;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.Order;
import com.music_shop.BL.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private final Logger log = new LoggerImpl(getClass().getName());

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getUserInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        User user = null;
        if (roles.contains(User.Role.EMPLOYEE.name())) {
            user = userService.getEmployeeByLogin(authentication.getName());
        } else if (roles.contains(User.Role.CUSTOMER.name())) {
            user = userService.getCustomerByLogin(authentication.getName());
        }
        model.addAttribute("user", user);
        return "user";
    }
}
