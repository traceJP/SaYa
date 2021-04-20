package com.tracejp.saya.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>本地文件路径统一管理类<p/>
 *
 * @author traceJP
 * @since 2021/4/17 12:54
 */
@Data
@Component
@ConfigurationProperties("local.file")
public class LocalPathProperties {

    /**
     * 用户头像路径
     */
    private String userAvatar;

    /**
     * 文件保存路径
     */
    private String fileSave;

}
