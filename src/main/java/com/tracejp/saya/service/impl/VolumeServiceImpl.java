package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.mapper.VolumeMapper;
import com.tracejp.saya.model.entity.Volume;
import com.tracejp.saya.model.properties.DefaultVolumeProperties;
import com.tracejp.saya.service.VolumeService;
import com.tracejp.saya.utils.SayaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class VolumeServiceImpl implements VolumeService {

    @Autowired
    private VolumeMapper volumeMapper;

    @Autowired
    private DefaultVolumeProperties defaultVolumeProperties;

    @Override
    public void createBy(Volume volume) {
        SayaUtils.influence(volumeMapper.insert(volume));
    }

    @Override
    public void createByDefault(String driveId) {
        Volume volume = new Volume();
        volume.setDriveId(driveId);
        volume.setVolumeCloudTotal(defaultVolumeProperties.getCloudTotal());
        volume.setVolumeCdnTotal(defaultVolumeProperties.getCdnTotal());
        volume.setVolumeCloudUsed(0L);
        volume.setVolumeCdnUsed(0L);
        createBy(volume);
    }

    @Override
    public void deleteBy(String driveId) {
        LambdaQueryWrapper<Volume> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Volume::getDriveId, driveId);
        SayaUtils.influence(volumeMapper.delete(wrapper));
    }

    @Override
    public void updateById(Volume volume) {
        SayaUtils.influence(volumeMapper.updateById(volume));
    }

    @Override
    public Optional<Volume> getBy(String driveId) {
        LambdaQueryWrapper<Volume> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Volume::getDriveId, driveId);
        return Optional.ofNullable(volumeMapper.selectOne(wrapper));
    }

}
