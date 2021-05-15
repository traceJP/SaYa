package com.tracejp.saya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tracejp.saya.model.entity.Volume;
import com.tracejp.saya.service.base.BaseService;

import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface VolumeService extends BaseService<Volume> {

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
     * 通过driveId获取
     * @param driveId 用户uuid
     * @return Volume
     */
    Optional<Volume> getBy(String driveId);

    /**
     * 分页查询记录
     * @param current 当前页
     * @param size 每页个数
     * @return IPage
     */
    IPage<Volume> listOfPage(long current, long size);

}
