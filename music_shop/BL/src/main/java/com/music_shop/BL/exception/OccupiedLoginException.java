package com.music_shop.BL.exception;

public class OccupiedLoginException extends RuntimeException {
    public OccupiedLoginException(String errorMessage) {
        super(errorMessage);
    }
}
