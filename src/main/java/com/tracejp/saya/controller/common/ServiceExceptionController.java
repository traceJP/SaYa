package com.tracejp.saya.controller.common;

import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.support.BadResponse;
import com.tracejp.saya.model.support.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>业务异常统一捕获<p/>
 * @author traceJP
 * @since 2021/4/17 11:07
 */
@RestControllerAdvice
public class ServiceExceptionController {

    /**
     * 业务逻辑异常统一捕获
     * @return 响应模板
     */
    @ExceptionHandler(ServiceException.class)
    public BaseResponse<?> serviceException(ServiceException e) {
        return BadResponse.bad(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public BaseResponse<?> notFoundException(NotFoundException e) {
        return BadResponse.bad(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
