package com.tracejp.saya.service;


import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.params.FileParam;
import com.tracejp.saya.model.params.UploadParam;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  文件服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface FileService {

    /**
     * 文件上传
     * @param param 文件上传参数
     * @return 上传成功返回File，未上传完成返回null，上传失败抛出异常
     */
    Optional<File> upload(UploadParam param);

    /**
     * 文件下载
     * @param fileHash 文件哈希
     */
    void download(String fileHash);

    /**
     * 做md5秒传
     * @param param md5值
     * @return 如果可以秒传则返回对应File，否则返回null
     */
    File md5SecondPass(UploadParam param);

    /**
     * 修改文件基本信息
     * @param file FileParam
     * @return File
     */
    File update(FileParam file);

    /**
     * 删除文件
     * @param fileHash 文件哈希
     */
    void deleteBy(String fileHash);

    /**
     * 通过id获取文件信息
     * @param id 自增id
     * @return File
     */
    File getById(Integer id);

    /**
     * 通过文件夹哈希获取文件列表
     * @param folderHash 文件夹哈希
     * @return 文件列表
     */
    List<File> listByFolder(String folderHash);

}
