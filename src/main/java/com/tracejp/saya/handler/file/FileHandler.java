package com.tracejp.saya.handler.file;

import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.support.TransportFile;
import com.tracejp.saya.model.support.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>文件处理器接口<p/>
 *
 * @author traceJP
 * @since 2021/4/19 20:19
 */
public interface FileHandler {

    /**
     * 获取规范目录
     * @param dir 目录绝对路径
     * @return 规范目录路径
     */
    static String normalizeDirectory(String dir) {
        if (StringUtils.isBlank(dir)) {
            throw new ServiceException("指定文件目录不能为空");
        }
        return StringUtils.appendIfMissing(dir, File.separator);
    }

    /**
     * 普通上传
     * @param file MultipartFile
     */
    void upload(MultipartFile file, String fileKey);

    /**
     * 分片上传
     * @param file UploadParam
     */
    UploadResult upload(UploadParam file);

    /**
     * 初始化分片上传
     * @return 需要保存的其他参数
     */
    default Map<String, Object> initUpload() {
        return new HashMap<>();
    }

    /**
     * 分片上传文件合并
     * @param results 文件上传结果集合
     * @param transportFile 文件传输实体信息
     */
    void merge(List<UploadResult> results, TransportFile transportFile);

    /**
     * 分片上传文件终止
     * @param transportFile 文件传输实体信息
     */
    void abort(TransportFile transportFile);

    /**
     * 通过文件哈希下载该文件
     * @param fileKey 文件哈希
     */
    default void download(String fileKey, Long totalSize, HttpServletResponse response) {
        download(fileKey, 0L, totalSize, response);
    }

    /**
     * 通过文件哈希下载指定字节的文件
     * @param fileKey 文件哈希
     * @param start 开始字节
     * @param end 结束字节
     */
    void download(String fileKey, Long start, Long end, HttpServletResponse response);

    /**
     * 获取当前实现类的类型
     * @return 处理器枚举
     */
    AttachmentType getAttachmentType();

}
