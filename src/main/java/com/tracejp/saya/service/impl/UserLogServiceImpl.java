package com.tracejp.saya.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.mapper.UserLogMapper;
import com.tracejp.saya.model.entity.UserLog;
import com.tracejp.saya.service.UserLogService;
import com.tracejp.saya.utils.SayaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    private UserLogMapper userLogMapper;


    @Override
    public void createBy(UserLog userLog) {
        SayaUtils.influence(userLogMapper.insert(userLog));
    }

    @Override
    public void deleteBy(String driveId) {
        LambdaQueryWrapper<UserLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserLog::getDriveId, driveId);
        SayaUtils.influence(userLogMapper.delete(wrapper));
    }

    @Override
    public List<UserLog> listBy(String driveId) {
        LambdaQueryWrapper<UserLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserLog::getDriveId, driveId);
        return userLogMapper.selectList(wrapper);
    }

    @Override
    public List<UserLog> listAll(String driveId) {
        LambdaQueryWrapper<UserLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserLog::getDriveId, driveId)
                .eq(UserLog::getDelFlag, "0")
                .eq(UserLog::getDelFlag, "1");
        return userLogMapper.selectList(wrapper);
    }

}
