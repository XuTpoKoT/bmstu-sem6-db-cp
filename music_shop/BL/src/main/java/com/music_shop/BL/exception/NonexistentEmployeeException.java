package com.music_shop.BL.exception;
public class NonexistentEmployeeException extends RuntimeException {
    public NonexistentEmployeeException(String errorMessage) {
        super(errorMessage);
    }
}
