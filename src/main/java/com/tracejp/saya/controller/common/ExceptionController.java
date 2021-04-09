package com.tracejp.saya.controller.common;

import com.tracejp.saya.model.support.BaseResponse;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author traceJP
 * @date 2021/4/9 18:50
 * controller层统一异常处理器
 */
@RestControllerAdvice
public class ExceptionController {

//     捕捉shiro的异常
//    @ExceptionHandler(ShiroException.class)
//    public BaseResponse<?> handle401() {
//        return new BaseResponse<?>().setStatus(HttpStatus.)
//    }

    /**
     * 所有控制层异常集中捕获（500）
     * @return 响应模板
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> globalException() {
        return new BaseResponse<>()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage("服务器内部出现问题");
    }

}
