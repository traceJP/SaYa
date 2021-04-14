package com.tracejp.saya.frame.shiro;

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author traceJP
 * @date 2021/4/10 22:19
 * 自定义shiro短信验证令牌
 */
@AllArgsConstructor
public class SmsToken implements AuthenticationToken {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 短信验证码
     */
    private String code;

    @Override
    public Object getPrincipal() {
        return phone;
    }

    @Override
    public Object getCredentials() {
        return code;
    }

}
