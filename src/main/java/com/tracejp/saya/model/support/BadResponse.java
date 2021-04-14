package com.tracejp.saya.model.support;

import org.springframework.http.HttpStatus;

/**
 * @author traceJP
 * @date 2021/4/11 22:09
 */
public class BadResponse extends BaseResponse<Object> {

    /**
     * 根据响应状态码和提示信息构造一个坏的响应
     * @param status 响应状态码
     * @param message 提示信息
     * @return
     */
    public static <T> BaseResponse<T> bad(HttpStatus status, String message) {
        return new BaseResponse<T>(status.value(), message, null);
    }

    /**
     * 直接构造一个坏的参数错误响应
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> badParams() {
        String msg = "请求失败，传入参数不完整";
        return bad(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 根据提示信息构造一个坏的响应
     * @param message 提示信息
     * @return
     */
    public static <T> BaseResponse<T> bad(String message) {
        return bad(HttpStatus.BAD_REQUEST, message);
    }

}
