package com.music_shop.BL.exception;
public class NonexistentEmployeeException extends RuntimeException {
    public NonexistentEmployeeException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentEmployeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentEmployeeException(Throwable cause) {
        super(cause);
    }
}
