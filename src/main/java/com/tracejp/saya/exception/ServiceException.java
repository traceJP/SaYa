package com.tracejp.saya.exception;

/**
 * <p>业务逻辑异常</p>
 *
 * @author traceJP
 * @since 2021/4/13 16:21
 */
public class ServiceException extends AbstractSayaException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
