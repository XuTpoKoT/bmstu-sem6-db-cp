package com.music_shop.BL.exception;

public class NonexistentLoginException extends RuntimeException {
    public NonexistentLoginException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentLoginException(Throwable cause) {
        super(cause);
    }
}
