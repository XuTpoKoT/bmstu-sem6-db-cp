package com.music_shop.mpa.controller;

import com.music_shop.BL.API.AuthService;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/registration/customer")
    public String getRegistrationPage() {
        return "registration";
    }

    @PostMapping("/registration/customer")
    public String registration(@RequestParam(name = "username") String login,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "firstName") String firstName,
                               @RequestParam(name = "lastName") String lastName,
                               @RequestParam(name = "email") String email,
                                Model model) {
        try {
            authService.registration(login, password, User.Role.CUSTOMER, firstName, lastName, email);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/registration";
        }
        return "redirect:/";
    }

    @GetMapping("/registration/employee")
    public String getRegistrationEmployeePage() {
        return "registrationEmployee";
    }

    @PostMapping("/registration/employee")
    public String registrationEmployee(@RequestParam(name = "username") String login,
                                       @RequestParam(name = "password") String password,
                                       Model model) {
        try {
            authService.registration(login, password, User.Role.EMPLOYEE, null, null, null);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registrationEmployee";
        }
        return "redirect:/";
    }
}
