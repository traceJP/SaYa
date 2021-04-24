package com.tracejp.saya.exception;

/**
 * <p>文件传输异常<p/>
 *
 * @author traceJP
 * @since 2021/4/24 18:20
 */
public class FileTransportException extends AbstractSayaException {

    public FileTransportException(String message) {
        super(message);
    }

    public FileTransportException(String message, Throwable cause) {
        super(message, cause);
    }

}
