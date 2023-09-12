package com.music_shop.BL.exception;

public class NonexistentDeliveryPointException extends RuntimeException {
    public NonexistentDeliveryPointException(String errorMessage) {
        super(errorMessage);
    }
}
