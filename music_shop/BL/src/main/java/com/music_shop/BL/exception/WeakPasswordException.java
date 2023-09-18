package com.music_shop.BL.exception;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String errorMessage) {
        super(errorMessage);
    }
    public WeakPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeakPasswordException(Throwable cause) {
        super(cause);
    }
}
