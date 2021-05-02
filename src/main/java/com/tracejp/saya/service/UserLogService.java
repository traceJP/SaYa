package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.UserLog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface UserLogService {

    /**
     * 创建,记录属性可为空
     * @param userLog userLog
     */
    void createBy(UserLog userLog);

    /**
     * 删除
     * @param driveId 用户uuid
     */
    void deleteBy(String driveId);

    /**
     * 通过driveId查询集合
     * @param driveId 用户uuid
     * @return UserLog集合
     */
    List<UserLog> listBy(String driveId);

    /**
     * 无视逻辑删除查询集合
     * @param driveId 用户uuid
     * @return UserLog集合
     */
    List<UserLog> listAll(String driveId);

}
