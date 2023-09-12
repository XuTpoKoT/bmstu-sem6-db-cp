package com.music_shop.BL.exception;

public class NonexistentCustomerException extends RuntimeException {
    public NonexistentCustomerException(String errorMessage) {
        super(errorMessage);
    }
}
