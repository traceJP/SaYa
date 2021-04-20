package com.tracejp.saya.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author traceJP
 * @date 2021/4/8 17:39
 */
@Component
@Data
@ConfigurationProperties("jwt.token")
public class JwtProperties {

    /**
     * token签名
     */
    private String sign = "saya";

    /**
     * token过期时间（毫秒）
     */
    private int expired = 1000;

    /**
     * token头部
     */
    private Map<String, Object> header;

}
