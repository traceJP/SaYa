package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.Recyclebin;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 回收站 服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface RecyclebinService {

    /**
     * 创建
     * @param recyclebin recyclebin
     */
    void createBy(Recyclebin recyclebin);

    /**
     * 删除
     * @param driveId 用户uuid
     * @param hashId 文件哈希
     */
    void deleteBy(String driveId, String hashId);

    /**
     * 通过用户id删除所有
     * @param driveId uuid
     */
    void deleteBy(String driveId);

    /**
     * 通过哈希id和类型获取（定位文件或文件夹）
     * @param hashId 哈希id
     * @param hashType 类型
     * @return Recyclebin
     */
    Optional<Recyclebin> getBy(String hashId, String hashType);

    /**
     * 通过用户uuid查询集合
     * @param driveId uuid
     * @return Recyclebin集合
     */
    List<Recyclebin> listByDrive(String driveId);

}
