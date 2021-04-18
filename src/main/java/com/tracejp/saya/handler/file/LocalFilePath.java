package com.tracejp.saya.handler.file;

import com.tracejp.saya.exception.ServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * <p>本地文件路径统一管理类<p/>
 *
 * @author traceJP
 * @since 2021/4/17 12:54
 */
@Component
@ConfigurationProperties("local.file")
@Data
@Slf4j
public class LocalFilePath {

    /**
     * 基本路径
     */
    private String basePath;

    /**
     * 用户头像路径
     */
    private String userAvatarPath;

    /**
     * 文件保存路径
     */
    private String fileSavePath;

    /**
     * 获取拼接的完整路径
     * @param pathName 路径名参数，即该类属性名
     * @return 绝对路径
     */
    @Cacheable(value = "local", key = "'path-' + #pathName")
    public String getPath(String pathName) {
        StringBuilder path = new StringBuilder();
        path.append(basePath);
        // 基本路径最后无分隔符
        if (!StringUtils.equals(basePath.substring(path.length() - 1, 1), File.separator)) {
            path.append(File.separator);
        }
        switch (pathName) {
            case "userAvatarPath" :
                path.append(userAvatarPath);
                break;
            case "fileSavePath" :
                path.append(fileSavePath);
                break;
            default:
                log.error("路径返回错误");
                throw new ServiceException("未找到本地配置文件对应路径");
        }
        if (!StringUtils.equals(basePath.substring(path.length() - 1, 1), File.separator)) {
            path.append(File.separator);
        }
        return path.toString();
    }

}
