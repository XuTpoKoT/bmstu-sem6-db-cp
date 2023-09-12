package com.music_shop.BL.exception;

public class DBException extends RuntimeException {
    public DBException(String errorMessage) {
        super(errorMessage);
    }
}
