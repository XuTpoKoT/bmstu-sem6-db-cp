package com.music_shop.BL.log;

public interface Logger {
    void debug(Object message);
    void info(Object message);
    void error(Object message);
    void error(Object message, Throwable throwable);
}
