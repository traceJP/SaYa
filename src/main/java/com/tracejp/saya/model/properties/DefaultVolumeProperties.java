package com.tracejp.saya.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>用户注册默认容量配置<p/>
 *
 * @author traceJP
 * @since 2021/5/2 12:02
 */
@Component
@Data
@ConfigurationProperties("user.volume")
public class DefaultVolumeProperties {

    /**
     * 云盘总容量 -> 默认20G
     */
    private Long cloudTotal = 21474836480L;

    /**
     * 云盘下载总容量 -> 默认5G
     */
    private Long cdnTotal = 5368709120L;

}
