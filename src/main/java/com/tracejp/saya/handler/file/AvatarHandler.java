package com.tracejp.saya.handler.file;

import cn.hutool.core.util.IdUtil;
import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>头像处理程序<p/>
 *
 * @author traceJP
 * @since 2021/4/17 14:45
 */
@Component
@Slf4j
public class AvatarHandler {

    @Autowired
    private LocalFilePath localFilePath;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 头像文件标记删除key： sys::avatardel
     */
    private static final String CACHE_DEL_AVATAR = RedisCacheKeys.SYSTEM_DOMAIN + RedisCacheKeys.DOMAIN +
            RedisCacheKeys.AVATAR_DEL;

    /**
     * 头像文件最大大小（字节） -> 5MB
     */
    public static final Integer AVATAR_MAX_SIZE = 1024 * 1024 * 5;

    /**
     * 检查并保存头像
     * @param avatar MultipartFile
     * @return AvatarResult
     */
    public Result checkAndSave(MultipartFile avatar) {
        if (check(avatar)) {
            return save(avatar);
        }
        return new Result().setSuccess(false);
    }

    /**
     * 检查头像是否合格
     * @param avatar MultipartFile
     * @return 合格返回true，否则返回false
     */
    public boolean check(MultipartFile avatar) {
        if (avatar == null || avatar.getSize() > AVATAR_MAX_SIZE) {
            return false;
        }
        // 查看contentType是否为图片类型
        if (avatar.getContentType() != null) {
            String type = avatar.getContentType().split("/")[0];
            return !StringUtils.equals(type, "image");
        }
        return true;
    }

    /**
     * 保存头像到本地
     * @param avatar MultipartFile
     * @return 保存结果
     */
    public Result save(MultipartFile avatar) {
        // 构造保存文件路径
        String randomFileName = IdUtil.fastSimpleUUID();
        if (avatar.getContentType() == null) {
            return new Result().setSuccess(false);
        }
        String suffix = "." + avatar.getContentType().split("/")[1];
        String path = localFilePath.getPath("userAvatarPath") + randomFileName + suffix;
        // 保存头像文件
        try {
            File file = new File(path);
            avatar.transferTo(file);
        } catch (IOException e) {
            log.warn("本地头像图片保存失败");
            return new Result().setSuccess(false);
        }
        return new Result(true, randomFileName, suffix);
    }

    /**
     * 标记需要删除的头像文件路径
     * @param path 头像文件路径
     */
    public void markDel(String path) {
        redisUtils.sSet(CACHE_DEL_AVATAR, path);
    }

    /**
     * 头像文件本地保存结果集
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public final class Result {

        /**
         * 头像本地保存是否成功
         */
        private Boolean success;

        /**
         * 本地保存名
         */
        private String localName;

        /**
         * 头像后缀，如 ”.jpg“
         */
        private String suffix;

        public String getAbsolutePath() {
            return localFilePath.getPath("userAvatarPath") + localName + suffix;
        }

        public String getRelativePath() {
            return localName + suffix;
        }

    }

}
