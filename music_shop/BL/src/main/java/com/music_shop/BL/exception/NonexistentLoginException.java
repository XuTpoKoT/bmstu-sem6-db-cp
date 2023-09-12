package com.music_shop.BL.exception;

public class NonexistentLoginException extends RuntimeException {
    public NonexistentLoginException(String errorMessage) {
        super(errorMessage);
    }
}
