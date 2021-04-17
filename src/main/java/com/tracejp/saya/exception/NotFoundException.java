package com.tracejp.saya.exception;

/**
 * <p>返回值为空-未找到异常<p/>
 *
 * @author traceJP
 * @since 2021/4/17 9:51
 */
public class NotFoundException extends AbstractSayaException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
