package com.music_shop.BL.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    public enum Role {
        CUSTOMER,
        EMPLOYEE,
        UNREGISTERED;
    }
    private String login;
    private byte[] password;
    private Role role;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String email;
    private Card card;


    @Builder
    public User(String login, byte[] password, Role role, String firstName, String lastName, String birthDate, String email) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
    }
}
