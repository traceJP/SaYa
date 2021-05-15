package com.tracejp.saya.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.mapper.FolderMapper;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.entity.Recyclebin;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.model.enums.YesNoStrEnum;
import com.tracejp.saya.model.params.FolderParam;
import com.tracejp.saya.service.FileService;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.service.RecyclebinService;
import com.tracejp.saya.service.base.impl.BaseServiceImpl;
import com.tracejp.saya.utils.SayaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
@Slf4j
public class FolderServiceImpl extends BaseServiceImpl<FolderMapper, Folder> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    @Lazy
    private FileService fileService;

    @Autowired
    private RecyclebinService recyclebinService;


    @Override
    public Folder createFolder(FolderParam folder) {
        if (StringUtils.isAllBlank(folder.getName(), folder.getParentHash())) {
            throw new ServiceException("传入参数错误");
        }

        // 检查是否存在父节点
        hasFolder(folder.getParentHash());

        // 构建entity新增记录
        Folder entity = folder.convertTo();
        entity.setDriveId(SayaUtils.getDriveId());
        entity.setIsRoot(YesNoStrEnum.NO.getValue());
        entity.setStarredFlag(YesNoStrEnum.NO.getValue());
        entity.setStatus(BaseStatusEnum.NORMAL.getValue());
        entity.setHash(IdUtil.fastSimpleUUID());
        SayaUtils.influence(folderMapper.insert(entity));

        return entity;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Folder createRoot(String driveId) {
        Folder entity = new Folder();
        entity.setDriveId(SayaUtils.getDriveId());
        entity.setIsRoot(YesNoStrEnum.YES.getValue());
        entity.setHash(ROOT_FOLDER_HASH);
        SayaUtils.influence(folderMapper.insert(entity));
        return entity;
    }

    @Override
    public Folder updateFolder(FolderParam folderInfo) {
        if (StringUtils.isAllBlank(folderInfo.getName(), folderInfo.getParentHash()) &&
                YesNoStrEnum.isNotInclude(folderInfo.getStarredFlag())) {
            throw new ServiceException("传入参数错误");
        }
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getHash, folderInfo.getHash());
        wrapper.eq(Folder::getDriveId, SayaUtils.getDriveId());
        SayaUtils.influence(folderMapper.update(folderInfo.convertTo(), wrapper));
        return getByHash(folderInfo.getHash()).orElseThrow(() -> new NotFoundException("文件夹基本信息未找到"));
    }

    @Override
    @Transactional
    public void deleteBy(String folderHash) {
        List<File> files = fileService.listByFolder(folderHash);
        // 删除所有文件夹内文件
        files.forEach((v) -> fileService.deleteBy(v.getHash()));
        // 删除文件夹
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getHash, folderHash);
        folderMapper.delete(wrapper);
    }

    @Override
    public Optional<Folder> getByHash(String folderHash) {
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getHash, folderHash);
        return Optional.of(folderMapper.selectOne(wrapper));
    }

    @Override
    public List<Object> getAll(String folderHash) {
        List<Object> res = new ArrayList<>();
        List<File> files = fileService.listByFolder(folderHash);
        List<Folder> folders = getList(folderHash);
        // 排除回收站内容
        List<Recyclebin> trashes = recyclebinService.listByDrive(SayaUtils.getDriveId());
        for (Recyclebin trash : trashes) {
            if (StringUtils.equals(trash.getHashType(), "1")) {
                // 文件排除
                files.removeIf(file -> StringUtils.equals(file.getHash(), trash.getHashId()));
            } else if (StringUtils.equals(trash.getHashType(), "2")) {
                // 文件夹排除
                folders.removeIf(folder -> StringUtils.equals(folder.getHash(), trash.getHashId()));
            }
        }
        res.addAll(folders);
        res.addAll(files);
        return res;
    }

    @Override
    public List<Folder> getList(String folderHash) {
        LambdaUpdateWrapper<Folder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Folder::getParentHash, folderHash);
        return folderMapper.selectList(wrapper);
    }

    @Override
    public void hasFolder(String folderHash) {
        LambdaQueryWrapper<Folder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Folder::getHash, folderHash);
        if (folderMapper.selectCount(wrapper) == 0) {
            log.warn("父文件节点不存在");
            throw new ServiceException("传入参数父文件夹不存在");
        }
    }

}
