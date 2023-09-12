package com.music_shop.BL.log;

public class LoggerImpl implements Logger{
    private final org.apache.log4j.Logger log;

    public LoggerImpl(String name) {
        log = org.apache.log4j.Logger.getLogger(name);
    }

    @Override
    public void debug(Object message) {
        log.debug(message);
    }

    @Override
    public void info(Object message) {
        log.info(message);
    }

    @Override
    public void error(Object message) {
        log.error(message);
    }

    @Override
    public void error(Object message, Throwable throwable) {
        log.error(message, throwable);
    }
}
