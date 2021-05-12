package com.tracejp.saya.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.MissingPropertyException;
import com.tracejp.saya.mapper.RecyclebinMapper;
import com.tracejp.saya.model.entity.Recyclebin;
import com.tracejp.saya.service.RecyclebinService;
import com.tracejp.saya.utils.SayaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 回收站 服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class RecyclebinServiceImpl implements RecyclebinService {

    @Autowired
    private RecyclebinMapper recyclebinMapper;


    @Override
    public void createBy(Recyclebin recyclebin) {
        if (Objects.isNull(recyclebin) || StringUtils.isAnyBlank(recyclebin.getDriveId(),
                recyclebin.getHashId(), recyclebin.getHashType())) {
            throw new MissingPropertyException("创建recyclebin记录存在属性为空");
        }
        if (!(StringUtils.equals(recyclebin.getHashType(), "1") || StringUtils.equals(recyclebin.getHashType(), "2"))) {
            throw new MissingPropertyException("创建recyclebin记录指定存储类型错误");
        }
        SayaUtils.influence(recyclebinMapper.insert(recyclebin));
    }

    @Override
    public void deleteBy(String hashType, String hashId) {
        if (StringUtils.isAnyBlank(hashType, hashId)) {
            throw new MissingPropertyException("删除用户记录时存在属性为空");
        }
        LambdaQueryWrapper<Recyclebin> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Recyclebin::getHashType, hashType);
        wrapper.eq(Recyclebin::getHashId, hashId);
        SayaUtils.influence(recyclebinMapper.delete(wrapper));
    }

    @Override
    public void deleteBy(String driveId) {
        if (StringUtils.isBlank(driveId)) {
            throw new MissingPropertyException("删除用户记录时存在属性为空");
        }
        LambdaQueryWrapper<Recyclebin> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Recyclebin::getDriveId, driveId);
        SayaUtils.influence(recyclebinMapper.delete(wrapper));
    }

    @Override
    public Optional<Recyclebin> getBy(String hashId, String hashType) {
        LambdaQueryWrapper<Recyclebin> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Recyclebin::getHashId, hashId);
        wrapper.eq(Recyclebin::getHashType, hashType);
        return Optional.ofNullable(recyclebinMapper.selectOne(wrapper));
    }

    @Override
    public List<Recyclebin> listByDrive(String driveId) {
        LambdaQueryWrapper<Recyclebin> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Recyclebin::getDriveId, driveId);
        return recyclebinMapper.selectList(wrapper);
    }

}
