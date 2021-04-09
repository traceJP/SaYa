package com.tracejp.saya.controller.common;

import com.tracejp.saya.model.support.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author traceJP
 * @date 2021/4/9 18:42
 * 通用控制层处理器
 */
@RestController()
public class CommonController {

    /**
     * 未经认证或授权的重定向接口
     * @param message 原因
     * @return 响应模板
     */
    @RequestMapping("/unauthorized/{message}")
    public BaseResponse<?> unauthorized(@PathVariable String message) {
        return new BaseResponse<>()
                .setStatus(HttpStatus.UNAUTHORIZED.value())
                .setMessage(message);
    }



}
