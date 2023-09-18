package com.music_shop.BL.exception;

public class NonexistentProductException extends RuntimeException {
    public NonexistentProductException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentProductException(Throwable cause) {
        super(cause);
    }
}
