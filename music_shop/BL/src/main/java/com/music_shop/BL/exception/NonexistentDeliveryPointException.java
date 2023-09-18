package com.music_shop.BL.exception;

public class NonexistentDeliveryPointException extends RuntimeException {
    public NonexistentDeliveryPointException(String errorMessage) {
        super(errorMessage);
    }
    public NonexistentDeliveryPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonexistentDeliveryPointException(Throwable cause) {
        super(cause);
    }
}
