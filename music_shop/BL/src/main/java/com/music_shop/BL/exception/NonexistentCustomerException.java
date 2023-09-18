package com.music_shop.BL.exception;

public class NonexistentCustomerException extends RuntimeException {
    public NonexistentCustomerException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentCustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentCustomerException(Throwable cause) {
        super(cause);
    }
}
