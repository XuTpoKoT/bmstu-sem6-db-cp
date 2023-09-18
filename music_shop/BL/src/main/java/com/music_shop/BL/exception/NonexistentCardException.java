package com.music_shop.BL.exception;

public class NonexistentCardException extends RuntimeException {
    public NonexistentCardException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentCardException(Throwable cause) {
        super(cause);
    }
}
