package com.music_shop.BL.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String errorMessage) {
        super(errorMessage);
    }
}
