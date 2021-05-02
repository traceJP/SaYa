package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.Volume;

import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface VolumeService {

    /**
     * 创建
     * @param volume Volume
     */
    void createBy(Volume volume);

    /**
     * 默认创建
     * @param driveId 用户id
     */
    void createByDefault(String driveId);

    /**
     * 删除
     * @param driveId 用户uuid
     */
    void deleteBy(String driveId);

    /**
     * 通过id修改
     * @param volume Volume
     */
    void updateById(Volume volume);

    /**
     * 通过driveId获取
     * @param driveId 用户uuid
     * @return Volume
     */
    Optional<Volume> getBy(String driveId);

}
