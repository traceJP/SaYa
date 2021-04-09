package com.tracejp.saya.config.shiro;

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author traceJP
 * @date 2021/4/8 16:19
 * 自定义shiro认证令牌
 */
@AllArgsConstructor
public class JwtToken implements AuthenticationToken {

    /**
     * jwt-token属性
     */
    private final String jwtToken;

    @Override
    public Object getPrincipal() {
        return jwtToken;
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

}
