package com.tracejp.saya.model.params;

import lombok.Data;

/**
 * <p>登录参数<p/>
 *
 * @author traceJP
 * @since 2021/5/17 9:35
 */
@Data
public class LoginParam {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String smsCode;

}
