package com.music_shop.BL.exception;

public class NonexistentCardException extends RuntimeException {
    public NonexistentCardException(String errorMessage) {
        super(errorMessage);
    }
}
