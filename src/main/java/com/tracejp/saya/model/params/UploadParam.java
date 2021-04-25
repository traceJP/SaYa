package com.tracejp.saya.model.params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>文件分片上传参数， 星号为必须<p/>
 *
 * @author traceJP
 * @since 2021/4/21 18:53
 */
@Data
public class UploadParam {

    /**
     * 是否开启分片上传 *
     */
    private Boolean enableChunk;

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
     * 相对路径（文件名.拓展名）, 文件key
     */
    private String relativePath;

    /**
     * 父文件夹哈希 *
     */
    private String folderHash;

    /**
     * 文件内容 *
     */
    private MultipartFile file;

    /**
     * 文件md5（用于秒传）
     */
    private String fileMd5;

}
