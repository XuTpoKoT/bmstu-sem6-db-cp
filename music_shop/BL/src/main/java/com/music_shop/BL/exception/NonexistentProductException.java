package com.music_shop.BL.exception;

public class NonexistentProductException extends RuntimeException {
    public NonexistentProductException(String errorMessage) {
        super(errorMessage);
    }
}
