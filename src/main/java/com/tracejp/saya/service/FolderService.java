package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.params.FolderAllQuery;
import com.tracejp.saya.model.params.FolderParam;
import com.tracejp.saya.model.params.base.BaseFileQuery;
import com.tracejp.saya.service.base.BaseService;

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
public interface FolderService extends BaseService<Folder> {
    /**
     * ROOT文件夹统一hash值
     */
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
    Folder createRoot(String driveId);

    /**
     * 修改文件夹基本信息
     * @param folderInfo 文件夹参数
     * @return 修改的文件夹实体
     */
    Folder updateFolder(FolderParam folderInfo);

    /**
     * 删除文件夹
     * @param folderHash 文件夹哈希
     */
    void deleteBy(String folderHash);

    /**
     * 通过文件夹哈希查找文件夹实体
     * @param folderHash 文件夹哈希
     * @return 文件夹实体
     */
    Optional<Folder> getByHash(String folderHash);

    /**
     * 查询文件夹所有内容（文件夹和文件）
     * @param query 查询参数
     * @return 文件内容列表
     */
    List<Object> getAll(FolderAllQuery query);

    /**
     * 查询文件夹内所有加星的内容（文件夹和文件）
     * @param query 查询参数
     * @return 文件夹列表
     */
    List<Object> getListByStar(BaseFileQuery query);

    /**
     * 查询文件夹中的所有文件夹
     * @param query 查询条件
     * @return 文件夹列表
     */
    List<Folder> getList(FolderAllQuery query);

    /**
     * 查询文件夹中的所有文件夹
     * @param folderHash 文件夹哈希
     * @return 文件夹列表
     */
    List<Folder> getList(String folderHash);

    /**
     * 检查文件夹节点是否存在
     * @param folderHash 文件夹哈希
     */
    void hasFolder(String folderHash);

    /**
     * 检查文件夹名是否存在问题，如果存在问题则抛出异常
     * @param name 文件夹名
     * @param parentHash 父文件哈希
     */
    void checkName(String name, String parentHash);

}
