package com.music_shop.BL.exception;

public class DBException extends RuntimeException {
    public DBException(String errorMessage) {
        super(errorMessage);
    }
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(Throwable cause) {
        super(cause);
    }
}
