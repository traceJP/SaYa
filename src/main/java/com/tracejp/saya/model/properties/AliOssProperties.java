package com.tracejp.saya.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>阿里OSS配置<p/>
 *
 * @author traceJP
 * @since 2021/4/23 17:10
 */
@Component
@Data
@ConfigurationProperties("ali.oss")
public class AliOssProperties {

    /**
     * 是否开启
     */
    private String enable;

    /**
     * 地域
     */
    private String endpoint;

    /**
     * 阿里AccessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * oss容器
     */
    private String bucketName;

}
