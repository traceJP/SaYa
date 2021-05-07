package com.tracejp.saya.handler.file;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
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
import com.tracejp.saya.utils.SayaUtils;
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
     * 文件上传有效时间 -> 1小时
     */
    private static final long UPLOAD_TIMEOUT = 60 << 2;


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
        if (param.getEnableChunk() && param.getTotalChunks() > 1) {

            // 首次分片上传
            if (param.getChunkNumber() == 1) {
                TransportFile file = initUpload(param);
                AttachmentType type = ValueEnum.valueToEnum(AttachmentType.class, file.getSaveType());
                UploadResult result = getSupportedType(type).upload(param, file);
                cacheSlice(param.getIdentifier(), result);
            } else {

                // 非首次上传
                TransportFile file = getFirstUpload(param.getIdentifier());
                if (Objects.nonNull(file)) {

                    AttachmentType type = ValueEnum.valueToEnum(AttachmentType.class, file.getSaveType());
                    String uploadKey = RedisCacheKeys.FILE_UPLOAD_PREFIX + param.getIdentifier();
                    String initKey = RedisCacheKeys.FILE_INIT_PREFIX + param.getIdentifier();

                    // 上传当前分片
                    UploadResult result = getSupportedType(type).upload(param, (TransportFile) redisUtils.get(initKey));
                    cacheSlice(param.getIdentifier(), result);

                    // 合并判断
                    boolean isMerge = false;
                    TransportFile transportFile = null;
                    List<UploadResult> list = null;
                    synchronized (this) {
                        if (redisUtils.lGetListSize(uploadKey) >= param.getTotalChunks()) {
                            isMerge = true;
                            transportFile = (TransportFile) redisUtils.get(initKey);
                            list = (List) redisUtils.lGet(uploadKey, 0, -1);
                            redisUtils.del(initKey, uploadKey);
                        }
                    }

                    // 合并操作
                    if (isMerge && Objects.nonNull(transportFile) && Objects.nonNull(list)) {
                        getSupportedType(type).merge(list, transportFile);
                        return transportFile;
                    }

                } else {

                    // 非首次上传，但无法获取到init缓存
                    ServletUtils.getCurrentResponse().orElseThrow(() -> new ServiceException("未找到请求体"))
                            .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    throw new FileTransportException("上传出现错误，请重新上传文件");
                }
            }

            return null;
        }

        // 单文件上传
        TransportFile entity = builderTransportFile(param);
        String fileKey = entity.getHash() + entity.getExtension();
        getSupportedType(ValueEnum.valueToEnum(AttachmentType.class, entity.getSaveType()))
                .upload(param.getFile(), fileKey);
        return entity;

    }

    /**
     * 文件下载， 存在对应请求头时可做分片下载
     * @param file 文件实体
     */
    public void doDownload(File file) {
        AttachmentType type = ValueEnum.valueToEnum(AttachmentType.class, file.getSaveType());

        HttpServletRequest request = ServletUtils.getCurrentRequest()
                .orElseThrow(() -> new ServiceException("未找到请求对象"));
        HttpServletResponse response = ServletUtils.getCurrentResponse()
                .orElseThrow(() -> new ServiceException("为找到响应对象"));

        long fileTotalSize = Long.parseLong(file.getSize());
        long startByte = 0L;
        long endByte = fileTotalSize;

        // 是否为分片下载
        String rangeHeader = ServletUtil.getHeader(request, "Range", StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(rangeHeader)) {

            // range规范解析
            String rangeVal = rangeHeader.replaceAll(" ", "")
                    .replaceAll("bytes=", "");
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
        response.addHeader("Content-Disposition", "attachment;filename=" + file.getName() +
                file.getExtension());
        String contentRange = startByte + "-" + endByte + "/" + fileTotalSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Content-Length", String.valueOf(endByte - startByte));

        // 交给对应文件处理器下载
        String fileKey = file.getHash() + file.getExtension();
        if (endByte - startByte == fileTotalSize) {
            getSupportedType(type).download(fileKey, endByte, response);
        } else {
            getSupportedType(type).download(fileKey, startByte, endByte, response);
        }

    }

    /**
     * 首次上传初始化
     * @param param UploadParam
     * @return 文件处理器
     */
    private TransportFile initUpload(UploadParam param) {

        // 构造初始化文件上传实体
        TransportFile entity = builderTransportFile(param);
        AttachmentType type = ValueEnum.valueToEnum(AttachmentType.class, entity.getSaveType());
        entity.setOtherParam(getSupportedType(type)
                .initUpload(entity.getHash() + entity.getExtension()));

        // 保存到redis中
        String key = RedisCacheKeys.FILE_INIT_PREFIX + param.getIdentifier();
        redisUtils.set(key, entity, UPLOAD_TIMEOUT);

        return entity;
    }

    /**
     * 构建传输对象
     * @param param UploadParam
     * @return TransportFile
     */
    public TransportFile builderTransportFile(UploadParam param) {

        TransportFile file = new TransportFile();

        // 选择分发文件处理器
        AttachmentType type = chooseType(param);
        file.setSaveType(type.getValue());

        // 构建基本属性
        file.setHash(IdUtil.fastSimpleUUID());
        file.setFolderHash(param.getFolderHash());
        file.setDriveId(SayaUtils.getDriveId());
        file.setStatus(BaseStatusEnum.NORMAL.getValue());
        file.setStarredFlag(YesNoStrEnum.NO.getValue());
        file.setMd5(param.getFileMd5());

        // 根据是否分片上传分片构建属性
        String fileKey;
        if (param.getEnableChunk()) {
            file.setUploadId(param.getIdentifier());
            file.setSize(String.valueOf(param.getTotalSize()));
            fileKey = param.getRelativePath();
        } else {
            file.setUploadId(IdUtil.fastSimpleUUID());
            file.setSize(String.valueOf(param.getFile().getSize()));
            fileKey = param.getFile().getOriginalFilename();
        }

        // 文件名解析
        if (StringUtils.isNotEmpty(fileKey)) {
            int lastPoint = fileKey.lastIndexOf('.');
            if (lastPoint == -1) {
                file.setName(fileKey);
                file.setExtension("");
            } else {
                file.setName(fileKey.substring(0, lastPoint - 1));
                file.setExtension(fileKey.substring(lastPoint));
            }
        } else {
            throw new FileTransportException("未能获取文件名，或者文件名为空");
        }

        return file;
    }

    /**
     * 处理器类型选择 - 当前实现，随机数分配
     * @param param 上传参数
     * @return AttachmentType
     */
    private AttachmentType chooseType(UploadParam param) {
        int random = RandomUtil.randomInt(1, fileHandlers.size() + 1);
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
     * 获取init缓存,获取重试周期200ms/1n，最大5次
     * @param uploadId 缓存键后缀-上传id
     * @return 存在缓存返回对应文件处理器枚举，不存在返回null
     */
    private TransportFile getFirstUpload(String uploadId) {
        String key = RedisCacheKeys.FILE_INIT_PREFIX + uploadId;
        try {
            for (int i = 0; i < 5; i++) {
                TransportFile transportFile = (TransportFile) redisUtils.get(key);
                if (Objects.nonNull(transportFile)) {
                    return transportFile;
                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            log.warn("文件上传线程中断异常");
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
