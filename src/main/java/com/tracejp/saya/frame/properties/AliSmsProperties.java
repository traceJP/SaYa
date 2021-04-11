package com.tracejp.saya.frame.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author traceJP
 * @date 2021/4/11 10:06
 */
@Component
@Data
@ConfigurationProperties("ali.sms")
public class AliSmsProperties {

    /**
     * 阿里AccessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * 访问域名
     */
    private String endpoint;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 模板代码，可以有多个模板
     * templateName:templateCode
     */
    private Map<String, String> templateCode;

}
