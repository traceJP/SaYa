package com.tracejp.saya.model.params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>文件分片上传参数<p/>
 *
 * @author traceJP
 * @since 2021/4/21 18:53
 */
@Data
public class UploadParam {

    /**
     * 当前分片数
     */
    private Integer chunkNumber;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 每片文件大小
     */
    private Integer chunkSize;

    /**
     * 当前片文件大小
     */
    private Integer currentChunkSize;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 上传编号ID
     */
    private String identifier;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 相对路径（文件名.拓展名）
     */
    private String relativePath;

    /**
     * 文件内容（需要手动设置）
     */
    private MultipartFile file;

    /**
     * 文件md5（用于秒传）
     */
    private String fileMd5;

    /**
     * 其他参数
     */
    private Map<String, Object> otherParam;

}
