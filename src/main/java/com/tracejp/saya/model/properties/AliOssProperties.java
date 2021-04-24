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





}
