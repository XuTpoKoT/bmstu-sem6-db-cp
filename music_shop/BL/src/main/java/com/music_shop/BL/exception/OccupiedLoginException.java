package com.music_shop.BL.exception;

public class OccupiedLoginException extends RuntimeException {
    public OccupiedLoginException(String errorMessage) {
        super(errorMessage);
    }
    public OccupiedLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public OccupiedLoginException(Throwable cause) {
        super(cause);
    }
}
