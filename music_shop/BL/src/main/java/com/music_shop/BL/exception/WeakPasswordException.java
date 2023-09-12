package com.music_shop.BL.exception;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String errorMessage) {
        super(errorMessage);
    }
}
