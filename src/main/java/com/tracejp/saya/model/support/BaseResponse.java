package com.tracejp.saya.model.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author traceJP
 * @date 2021/4/8 10:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseResponse<T> {

    /**
     * 响应状态码
     */
    private Integer status;

    /**
     * 提示字符串
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 根据提示字符串和响应数据构建一个ok返回状态
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> ok(String message, T data) {
        return new BaseResponse<T>(HttpStatus.OK.value(), message, data);
    }

    /**
     * 根据提示字符串构建一个ok返回状态
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> ok(String message) {
        return ok(message, null);
    }

    /**
     * 只根据响应数据构建一个ok返回状态
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }

}
