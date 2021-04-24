package com.tracejp.saya.handler.file;

import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.support.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        return StringUtils.appendIfMissing(dir, File.pathSeparator);
    }

    /**
     * 普通上传
     * @param file
     */
    void upload(MultipartFile file);

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
     */
    void merge(Set<Object> results);

    /**
     * 分片上传文件终止
     * @param file UploadParam
     */
    void abort(UploadParam file);

    /**
     * 通过文件哈希下载该文件
     * @param fileHash 文件哈希
     * @return 整个文件的输入流
     */
    default void download(String fileHash, HttpServletResponse response) {
        download(fileHash, 0L, Long.MAX_VALUE, response);
    }

    /**
     * 通过文件哈希下载指定字节的文件
     * @param fileHash 文件哈希
     * @param start 开始字节
     * @param end 结束字节
     * @return 指定字节的输入流
     */
    void download(String fileHash, Long start, Long end, HttpServletResponse response);

    /**
     * 通过文件哈希删除一个文件
     * @param fileHash 文件哈希
     */
    void delete(String fileHash);

    /**
     * 数据库记录添加
     * @param fileHash 记录哈希
     */
    void preserve(String fileHash);

    /**
     * 获取当前实现类的类型
     * @return 处理器枚举
     */
    AttachmentType getAttachmentType();

}
