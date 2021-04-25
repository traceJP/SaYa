package com.tracejp.saya.handler.file;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.tracejp.saya.exception.FileTransportException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.model.enums.ValueEnum;
import com.tracejp.saya.model.enums.YesNoStrEnum;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.support.TransportFile;
import com.tracejp.saya.model.support.UploadResult;
import com.tracejp.saya.utils.RedisUtils;
import com.tracejp.saya.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
     * @return 上传成功则返回上传的文件所有参数，未上传完成返回null，上传失败抛出异常
     */
    public TransportFile doUpload(UploadParam param) {

        // 做分片上传
        if (param.getEnableChunk()) {
            AttachmentType type = isFirstUpload(param.getIdentifier());

            // 是否第一次上传
            if (type != null) {
                String key = RedisCacheKeys.FILE_UPLOAD_PREFIX + param.getIdentifier();

                // 文件是否可以合并
                if (redisUtils.sGetSetSize(key) == param.getTotalChunks()) {
                    TransportFile transportFile = (TransportFile) redisUtils.get(key);
                    // TODO: 2021/4/25 需要改成安全转换
                    List<UploadResult> list = (List<UploadResult>)(List) redisUtils.lGet(key, 0, -1);
                    getSupportedType(type).merge(list, transportFile);
                    cacheClear(key);
                    return transportFile;
                }
            } else if (param.getChunkNumber() == 1) {

                // 是分片上传的第一片
                type = initUpload(param);
            } else {
                ServletUtils.getCurrentResponse().orElseThrow(() -> new ServiceException("未找到请求体"))
                        .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                throw new FileTransportException("上传超时，请重新上传整个文件");
            }

            // 上传当前文件
            UploadResult result = getSupportedType(type).upload(param);

            // 记录上传片
            cacheSlice(param.getIdentifier(), result);

            return null;
        }

        // 单文件上传
        TransportFile entity = builderTransportFile(param);
        String fileKey = entity.getFileHash() + entity.getFileExtension();
        getSupportedType(ValueEnum.valueToEnum(AttachmentType.class, entity.getFileSaveType()))
                .upload(param.getFile(), fileKey);
        return entity;

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
    private AttachmentType initUpload(UploadParam param) {

        // 构造初始化文件上传实体
        TransportFile entity = builderTransportFile(param);

        // 保存到redis中
        String key = RedisCacheKeys.FILE_INIT_PREFIX + param.getIdentifier();
        redisUtils.set(key, entity, UPLOAD_TIMEOUT);

        return ValueEnum.valueToEnum(AttachmentType.class, entity.getFileSaveType());
    }

    /**
     * 构建传输对象
     * @param param UploadParam
     * @return TransportFile
     */
    private TransportFile builderTransportFile(UploadParam param) {

        TransportFile file = new TransportFile();

        // 选择分发文件处理器
        AttachmentType type = chooseType(param);
        file.setFileSaveType(type.getValue());

        // 构建基本属性
        file.setFileHash(IdUtil.fastSimpleUUID());
        file.setFolderHash(param.getFolderHash());
        file.setFileStatus(BaseStatusEnum.NORMAL.getValue());
        file.setStarredFlag(YesNoStrEnum.NO.getValue());

        // 根据是否分片上传分片构建属性
        String fileKey;
        if (param.getEnableChunk()) {
            file.setFileUploadId(param.getIdentifier());
            file.setFileMd5(param.getFileMd5());
            file.setFileSize(String.valueOf(param.getTotalSize()));
            // 文件处理器初始化参数保存
            file.setOtherParam(getSupportedType(type).initUpload());
            fileKey = param.getRelativePath();
        } else {
            file.setFileUploadId(IdUtil.fastSimpleUUID());
            try {
                file.setFileMd5(DigestUtil.md5Hex(param.getFile().getBytes()));
            } catch (IOException e) {
                log.warn("普通上传：获取上传文件字节数组出现异常");
            }
            file.setFileSize(String.valueOf(param.getFile().getSize()));
            fileKey = param.getFile().getName();
        }

        // 文件名解析
        int lastPoint = fileKey.lastIndexOf('.');
        if (lastPoint == -1) {
            file.setFileName(fileKey);
            file.setFileExtension("");
        } else {
            file.setFileName(fileKey.substring(0, lastPoint - 1));
            file.setFileExtension(fileKey.substring(lastPoint));
        }
        return file;

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
        redisUtils.lSet(key, result, UPLOAD_TIMEOUT);
    }

    /**
     * 清除所有缓存
     * @param uploadId 上传id
     */
    private void cacheClear(String uploadId) {
        String initKey = RedisCacheKeys.FILE_INIT_PREFIX + uploadId;
        String uploadKey = RedisCacheKeys.FILE_UPLOAD_PREFIX + uploadId;

        // 交给对应文件处理器做清理动作
        TransportFile transportFile = (TransportFile) redisUtils.get(initKey);
        getSupportedType(ValueEnum.valueToEnum(AttachmentType.class, transportFile.getFileSaveType()))
                .abort(transportFile);

        // 清除redis缓存
        redisUtils.del(initKey, uploadKey);
    }

    /**
     * 是否存在缓存（是否是第一次上传）
     * @param uploadId 缓存键后缀-上传id
     * @return 存在缓存返回true，不存在返回false
     */
    private AttachmentType isFirstUpload(String uploadId) {
        String key = RedisCacheKeys.FILE_INIT_PREFIX + uploadId;
        TransportFile transportFile = (TransportFile) redisUtils.get(key);
        if (Objects.nonNull(transportFile)) {
            String type = transportFile.getFileSaveType();
            return ValueEnum.valueToEnum(AttachmentType.class, type);
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
     * 获取文件处理程序集合中支持的类型对象，不存在时抛出异常
     * @param type 类型对象
     * @return 对应的文件处理器
     */
    private FileHandler getSupportedType(AttachmentType type) {
        FileHandler handler = fileHandlers.get(type);
        if (handler == null) {
            String msg = "文件处理程序不存在，请检查配置文件";
            log.error(msg);
            throw new ServiceException(msg);
        }
        return handler;
    }

}
