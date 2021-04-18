package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.params.FolderParam;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface FolderService {

    String ROOT_FOLDER_HASH = "root";

    /**
     * 创建一个文件夹（非根目录）
     * @param folder 文件夹参数
     * @return 创建的文件夹实体
     */
    Folder createFolder(FolderParam folder);

    /**
     * 创建一个文件夹（根目录）
     * @return 成功则创建的文件夹实体
     */
    Folder createFolder();

    /**
     * 修改文件夹基本信息
     * @param folderInfo 文件夹参数
     * @param folderHash 文件夹哈希
     * @return 修改的文件夹实体
     */
    Folder updateFolder(FolderParam folderInfo, String folderHash);

    /**
     * 通过文件夹哈希查找文件夹实体
     * @param folderHash 文件夹哈希
     * @return 文件夹实体
     */
    Optional<Folder> getByHash(String folderHash);

    /**
     * 查询文件夹所有内容（文件夹和文件）
     * @param folderHash 文件夹哈希
     * @return 文件内容列表
     */
    List<Object> getAll(String folderHash);

    /**
     * 查询文件夹中的所有文件夹
     * @param folderHash 文件夹哈希
     * @return 文件夹列表
     */
    List<Folder> getList(String folderHash);

}
