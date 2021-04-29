package com.tracejp.saya.exception;

/**
 * <p>缺少属性值异常<p/>
 *
 * @author traceJP
 * @since 2021/4/27 16:30
 */
public class MissingPropertyException extends AbstractSayaException {

    public MissingPropertyException(String message) {
        super(message);
    }

    public MissingPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

}
