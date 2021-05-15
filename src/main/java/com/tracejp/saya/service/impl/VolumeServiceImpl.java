package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tracejp.saya.mapper.VolumeMapper;
import com.tracejp.saya.model.entity.Volume;
import com.tracejp.saya.model.properties.DefaultVolumeProperties;
import com.tracejp.saya.service.VolumeService;
import com.tracejp.saya.service.base.impl.BaseServiceImpl;
import com.tracejp.saya.utils.SayaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class VolumeServiceImpl extends BaseServiceImpl<VolumeMapper, Volume> implements VolumeService {

    @Autowired
    private VolumeMapper volumeMapper;

    @Autowired
    private DefaultVolumeProperties defaultVolumeProperties;

    @Override
    public void createBy(Volume volume) {
        SayaUtils.influence(volumeMapper.insert(volume));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createByDefault(String driveId) {
        Volume volume = new Volume();
        volume.setDriveId(driveId);
        volume.setCloudTotal(defaultVolumeProperties.getCloudTotal());
        volume.setCdnTotal(defaultVolumeProperties.getCdnTotal());
        volume.setCloudUsed(0L);
        volume.setCdnUsed(0L);
        createBy(volume);
    }

    @Override
    public void deleteBy(String driveId) {
        LambdaQueryWrapper<Volume> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Volume::getDriveId, driveId);
        SayaUtils.influence(volumeMapper.delete(wrapper));
    }

    @Override
    public Optional<Volume> getBy(String driveId) {
        LambdaQueryWrapper<Volume> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Volume::getDriveId, driveId);
        return Optional.ofNullable(volumeMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Volume> listOfPage(long current, long size) {
        return volumeMapper.selectPage(new Page<>(current, size), null);
    }

}
