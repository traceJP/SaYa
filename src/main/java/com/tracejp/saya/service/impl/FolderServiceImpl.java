package com.tracejp.saya.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.mapper.FolderMapper;
import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.model.enums.YesNoStrEnum;
import com.tracejp.saya.model.params.FolderParam;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.utils.SayaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderMapper folderMapper;

    @Override
    public Folder createFolder(FolderParam folder) {
        if (StringUtils.isAllBlank(folder.getFolderName(), folder.getFolderParentHash())) {
            throw new ServiceException("传入参数错误");
        }
        // 构建entity新增记录
        Folder entity = folder.convertTo();
        entity.setDriveId(SayaUtils.getDriveId());
        entity.setFolderRoot(YesNoStrEnum.NO.getValue());
        entity.setStarredFlag(YesNoStrEnum.NO.getValue());
        entity.setFolderStatus(BaseStatusEnum.NORMAL.getValue());
        entity.setFolderHash(IdUtil.fastSimpleUUID());
        SayaUtils.influence(folderMapper.insert(entity));

        return entity;
    }

    @Override
    public Folder createRoot() {
        Folder entity = new Folder();
        entity.setDriveId(SayaUtils.getDriveId());
        entity.setFolderRoot(YesNoStrEnum.YES.getValue());
        entity.setFolderHash(ROOT_FOLDER_HASH);
        SayaUtils.influence(folderMapper.insert(entity));
        return entity;
    }

    @Override
    public Folder updateFolder(FolderParam folderInfo, String folderHash) {
        if (StringUtils.isAllBlank(folderInfo.getFolderName(), folderInfo.getFolderParentHash()) &&
                YesNoStrEnum.isNotInclude(folderInfo.getStarredFlag())) {
            throw new ServiceException("传入参数错误");
        }
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getFolderHash, folderHash);
        wrapper.eq(Folder::getDriveId, SayaUtils.getDriveId());
        SayaUtils.influence(folderMapper.update(folderInfo.convertTo(), wrapper));
        return getByHash(folderHash).orElseThrow(() -> new NotFoundException("文件夹基本信息未找到"));
    }

    @Override
    public Optional<Folder> getByHash(String folderHash) {
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getFolderHash, folderHash);
        return Optional.of(folderMapper.selectOne(wrapper));
    }

    @Override
    public List<Object> getAll(String folderHash) {
        return null;
    }

    @Override
    public List<Folder> getList(String folderHash) {
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getFolderHash, folderHash);
        return folderMapper.selectList(wrapper);
    }

}
