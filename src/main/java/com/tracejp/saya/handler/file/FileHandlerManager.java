package com.tracejp.saya.handler.file;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.tracejp.saya.exception.FileTransportException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.enums.ValueEnum;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.support.UploadResult;
import com.tracejp.saya.utils.RedisUtils;
import com.tracejp.saya.utils.ServletUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>文件处理程序管理器<p/>
 *
 * @author traceJP
 * @since 2021/4/19 20:24
 */
@Component
@Slf4j
public class FileHandlerManager {

    /**
     * 注册FileHandler对象统一管理集合
     */
    private final ConcurrentHashMap<AttachmentType, FileHandler> fileHandlers = new ConcurrentHashMap<>(16);

    /**
     * 文件上传有效时间 -> 3小时
     */
    private static final long UPLOAD_TIMEOUT = 60 << 2 * 3;


    @Autowired
    private RedisUtils redisUtils;

    /**
     * 注册所有实现了FileHandler接口的组件
     * @param applicationContext spring上下文
     */
    private FileHandlerManager(ApplicationContext applicationContext) {
        addFileHandlers(applicationContext.getBeansOfType(FileHandler.class).values());
        log.info("已经注册{}个FileHandler组件", fileHandlers.size());
    }

    /**
     * 文件上传
     * @param param 上传参数
     */
    public UploadInit doUpload(UploadParam param) {
        UploadInit initEntity = (UploadInit) param;

        AttachmentType type = isFirstUpload(param.getIdentifier());
        if (type != null) {
            // 是否全部上传完成
            String key = RedisCacheKeys.FILE_UPLOAD_PREFIX + param.getIdentifier();
            if (redisUtils.sGetSetSize(key) == param.getTotalChunks()) {
                getSupportedType(type).merge(redisUtils.sGet(key));
                cacheClear(key);
                initEntity.setAllSuccess(true);
                return initEntity;
            }
        } else if (param.getChunkNumber() == 1) {    // 是分片上传的第一片
            type = doInitUpload(param);
        } else {
            ServletUtils.getCurrentResponse().orElseThrow(() -> new ServiceException("未找到请求体"))
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new FileTransportException("上传超时，请重新上传整个文件");
        }

        // 上传当前文件
        UploadResult result = getSupportedType(type).upload(param);

        // 记录上传片
        cacheSlice(param.getIdentifier(), result);

        initEntity.setAllSuccess(false);
        return initEntity;
    }

    /**
     * 文件下载， 存在对应请求头时可做分片下载
     * @param file 文件实体
     */
    public void doDownload(File file) {
        AttachmentType type = ValueEnum.valueToEnum(AttachmentType.class, file.getFileSaveType());

        HttpServletRequest request = ServletUtils.getCurrentRequest().orElseThrow(
                () -> new ServiceException("未找到请求对象"));
        HttpServletResponse response = ServletUtils.getCurrentResponse().orElseThrow(
                () -> new ServiceException("为找到响应对象"));

        long fileTotalSize = Long.parseLong(file.getFileSize());
        long startByte = 0L;
        long endByte = fileTotalSize;

        // 是否为分片下载
        String rangeHeader = ServletUtil.getHeader(request, "Range", StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(rangeHeader)) {

            // range规范解析
            String rangeVal = rangeHeader.replaceAll(" ", "").replaceAll("bytes=", "");
            String[] range = rangeVal.split("-");
            if (range.length == 0) {
                throw new FileTransportException("分片下载信息格式错误");
            } else if (range.length == 1) {
                if (rangeVal.charAt(0) == '-') {
                    // 最后n字节
                    startByte = fileTotalSize - Integer.parseInt(range[0]);
                } else if (rangeVal.charAt(rangeVal.length() - 1) == '-') {
                    // n字节以后的范围
                    startByte = Integer.parseInt(range[0]);
                } else {
                    throw new FileTransportException("分片下载信息格式错误");
                }
            } else if (range.length == 2) {
                startByte = Integer.parseInt(range[0]);
                endByte = Integer.parseInt(range[1]);
            } else {
                throw new FileTransportException("分片下载信息格式错误");
            }

            // 设置分片下载响应头信息
            response.setHeader("Accept-Range", "bytes");
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        }

        // 设置下载基本响应头
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;filename=" + file.getFileName() +
                file.getFileExtension());
        String contentRange = startByte + "-" + endByte + "/" + fileTotalSize;
        response.setHeader("Content-Range", contentRange);
        long rangeSize = endByte - startByte + 1;
        response.setHeader("Content-Length", String.valueOf(rangeSize));

        // 交给对应文件处理器下载
        if (endByte - startByte == fileTotalSize) {
            getSupportedType(type).download(file.getFileHash(), response);
        } else {
            getSupportedType(type).download(file.getFileHash(), startByte, endByte, response);
        }

    }

    /**
     * 首次上传初始化
     * @param param UploadParam
     * @return 文件处理器
     */
    private AttachmentType doInitUpload(UploadParam param) {
        // 选择分发文件处理器
        AttachmentType type = chooseType(param);

        // 构造redis-key
        String key = RedisCacheKeys.FILE_INIT_PREFIX + param.getIdentifier();

        // 构造初始化文件上传实体
        UploadInit initEntity = (UploadInit) param;
        initEntity.setFileHandler(type);
        initEntity.setOtherParam(getSupportedType(type).initUpload());

        // 保存到redis中
        redisUtils.set(key, initEntity, UPLOAD_TIMEOUT);

        return type;
    }

    /**
     * 处理器类型选择 - 当前实现，随机数分配
     * @param param 上传参数
     * @return AttachmentType
     */
    private AttachmentType chooseType(UploadParam param) {
        int random = RandomUtil.randomInt(1, fileHandlers.size());
        return ValueEnum.valueToEnum(AttachmentType.class, String.valueOf(random));
    }

    /**
     * 分片上传缓存记录
     * @param uploadId 缓存键后缀-上传id
     * @param result 上传结果集
     */
    private void cacheSlice(String uploadId, UploadResult result) {
        String key = RedisCacheKeys.FILE_UPLOAD_PREFIX + uploadId;
        redisUtils.sSetAndTime(key, UPLOAD_TIMEOUT, result);
    }

    /**
     * 清除所有缓存
     * @param uploadId 上传id
     */
    private void cacheClear(String uploadId) {
        String initKey = RedisCacheKeys.FILE_INIT_PREFIX + uploadId;
        String uploadKey = RedisCacheKeys.FILE_UPLOAD_PREFIX + uploadId;
        redisUtils.del(initKey, uploadKey);
    }

    /**
     * 是否存在缓存（是否是第一次上传）
     * @param uploadId 缓存键后缀-上传id
     * @return 存在缓存返回true，不存在返回false
     */
    private AttachmentType isFirstUpload(String uploadId) {
        String key = RedisCacheKeys.FILE_INIT_PREFIX + uploadId;
        UploadInit initEntity = (UploadInit) redisUtils.get(key);
        if (Objects.nonNull(initEntity)) {
            return initEntity.getFileHandler();
        }
        return null;
    }

    /**
     * bean对象创建时的所有合格bean统一注册
     * @param fileHandlers 文件处理程序集合
     * @return 当前类对象
     */
    private FileHandlerManager addFileHandlers(Collection<FileHandler> fileHandlers) {
        if (!CollectionUtils.isEmpty(fileHandlers)) {
            for (FileHandler handler : fileHandlers) {
                if (this.fileHandlers.containsKey(handler.getAttachmentType())) {
                    String msg = "文件处理器异常组件注册重复";
                    log.warn(msg + "，具体重复组件类型为{}", handler.getAttachmentType());
                    throw new ServiceException(msg);
                }
                this.fileHandlers.put(handler.getAttachmentType(), handler);
            }
        }
        return this;
    }

    /**
     * 获取文件处理程序集合中支持的类型对象，如果不存在对应处理器，则默认返回本地文件处理器
     * @param type 类型对象
     * @return 对应的文件处理器
     */
    private FileHandler getSupportedType(AttachmentType type) {
        FileHandler handler = fileHandlers.getOrDefault(type, fileHandlers.get(AttachmentType.LOCAL));
        if (handler == null) {
            String msg = "没有可用的文件处理程序";
            log.error(msg);
            throw new ServiceException(msg);
        }
        return handler;
    }

    /**
     * redis初始化保存实体
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public class UploadInit extends UploadParam {

        /**
         * 所有文件上传成功
         */
        private boolean allSuccess;

        /**
         * 文件处理器类型
         */
        private AttachmentType fileHandler;

    }

}
