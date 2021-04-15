package com.tracejp.saya.exception;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/15 20:27
 */
public abstract class AbstractSayaException extends RuntimeException {

    public AbstractSayaException(String message) {
        super(message);
    }

    public AbstractSayaException(String message, Throwable cause) {
        super(message, cause);
    }

}
