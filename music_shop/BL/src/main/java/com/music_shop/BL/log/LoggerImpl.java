package com.music_shop.BL.log;

import org.slf4j.LoggerFactory;

public class LoggerImpl implements com.music_shop.BL.log.Logger {
    private final org.slf4j.Logger log;

    public LoggerImpl(String className) {
        log = LoggerFactory.getLogger(className);
    }

    @Override
    public void debug(Object message) {
        log.debug((String) message);
    }

    @Override
    public void info(Object message) {
        log.info((String) message);
    }

    @Override
    public void error(Object message) {
        log.error((String) message);
    }

    @Override
    public void error(Object message, Throwable throwable) {
        log.error((String) message, throwable);
    }
}
