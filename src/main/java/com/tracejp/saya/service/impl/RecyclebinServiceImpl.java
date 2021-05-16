package com.tracejp.saya.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.MissingPropertyException;
import com.tracejp.saya.mapper.RecyclebinMapper;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.entity.Recyclebin;
import com.tracejp.saya.model.enums.OrderEnum;
import com.tracejp.saya.model.params.base.BaseFileQuery;
import com.tracejp.saya.service.FileService;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.service.RecyclebinService;
import com.tracejp.saya.service.base.impl.BaseServiceImpl;
import com.tracejp.saya.utils.SayaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class RecyclebinServiceImpl extends BaseServiceImpl<RecyclebinMapper, Recyclebin> implements RecyclebinService {

    @Autowired
    private RecyclebinMapper recyclebinMapper;

    @Autowired
    @Lazy
    private FileService fileService;

    @Autowired
    @Lazy
    private FolderService folderService;

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

    @Override
    public List<Object> listOfFolderContent(BaseFileQuery query) {
        String drive = SayaUtils.getDriveId();
        List<Recyclebin> list = listByDrive(drive);

        // 区分file和folder
        List<String> fileHash = new ArrayList<>();
        List<String> folderHash = new ArrayList<>();
        list.forEach(v -> {
            if (StringUtils.equals(v.getHashType(), "1")) {
                fileHash.add(v.getHashId());
            } else if (StringUtils.equals(v.getHashType(), "2")) {
                folderHash.add(v.getHashId());
            }
        });

        List<Object> res = new ArrayList<>();

        // 拼接file查询条件
        if (!fileHash.isEmpty()) {
            QueryWrapper<File> fileWrapper = new QueryWrapper<>();
            fileWrapper.orderBy(true, query.isAsc(), query.getOrderBy().getValue());
            fileWrapper.lambda()
                    .in(File::getHash, fileHash)
                    .eq(File::getDriveId, drive);
            List<File> files = fileService.list(fileWrapper);
            res.addAll(files);
        }

        // 拼接folder查询条件
        if (!folderHash.isEmpty()) {
            QueryWrapper<Folder> folderWrapper = new QueryWrapper<>();
            if (query.getOrderBy() != OrderEnum.FILE_SIZE) {
                folderWrapper.orderBy(true, query.isAsc(), query.getOrderBy().getValue());
            }
            folderWrapper.lambda()
                    .in(Folder::getHash, folderHash)
                    .eq(Folder::getDriveId, drive);
            List<Folder> folders = folderService.list(folderWrapper);
            res.addAll(folders);
        }

        return res;
    }

}
