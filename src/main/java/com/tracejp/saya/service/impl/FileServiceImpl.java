package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.FileTransportException;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.handler.file.FileHandlerManager;
import com.tracejp.saya.mapper.FileMapper;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.entity.Volume;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.model.params.FileParam;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.service.FileService;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.service.VolumeService;
import com.tracejp.saya.utils.SayaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FolderService folderService;

    @Autowired
    private VolumeService volumeService;

    @Autowired
    private FileHandlerManager fileHandler;


    @Override
    public Optional<File> upload(UploadParam param) {

        // 父文件夹检测处理
        folderService.hasFolder(param.getFolderHash());

        // 用户云盘容量检查
        Volume userVolume = volumeService.getBy(SayaUtils.getDriveId())
                .orElseThrow(() -> new ServiceException("未找到用户容量记录"));
        long newCloudVolume = param.getTotalSize() + userVolume.getCloudUsed();
        if (newCloudVolume > userVolume.getCloudTotal()) {
            throw new ServiceException("当前云盘容量使用已满");
        }

        // 秒传处理
        File md5file = md5SecondPass(param);
        if (Objects.nonNull(md5file)) {
            return Optional.of(md5file);
        }

        // 文件上传
        File upload = fileHandler.doUpload(param);

        // 上传成功
        if (Objects.nonNull(upload)) {
            SayaUtils.influence(fileMapper.insert(upload));
            // 新容量计算
            userVolume.setCloudUsed(newCloudVolume);
            volumeService.updateById(userVolume);
        }

        return Optional.ofNullable(upload);
    }

    @Override
    public void download(String fileHash) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getHash, fileHash);
        wrapper.eq(File::getDriveId, SayaUtils.getDriveId());
        File file = fileMapper.selectOne(wrapper);

        if (Objects.isNull(file)) {
            throw new FileTransportException("未找到文件记录");
        }

        // 文件状态检查
        if (StringUtils.equals(file.getStatus(), BaseStatusEnum.DEACTIVATE.getValue())) {
            throw new FileTransportException("当前下载文件被封禁存在异常");
        }

        // 用户下载容量检查
        Volume userVolume = volumeService.getBy(SayaUtils.getDriveId())
                .orElseThrow(() -> new FileTransportException("未找到用户容量记录表"));
        long newCdnVolume = Long.parseLong(file.getSize()) + userVolume.getCdnUsed();
        if (newCdnVolume > userVolume.getCdnTotal()) {
            throw new FileTransportException("当前下载容量不足");
        }

        // 文件下载
        fileHandler.doDownload(file);

        // 新容量计算
        userVolume.setCdnUsed(newCdnVolume);
        volumeService.updateById(userVolume);

    }

    @Override
    public File md5SecondPass(UploadParam param) {
        if (!param.getEnableChunk() || param.getChunkNumber() == 1) {
            LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(File::getMd5, param.getFileMd5());
            File file = fileMapper.selectOne(wrapper);

            // 存在相同md5值
            if (Objects.nonNull(file)) {
                // 关闭分片上传，以免调用文件处理器init方法
                param.setEnableChunk(false);
                File upload = fileHandler.builderTransportFile(param);
                // 原属性覆盖并保存
                upload.setFolderHash(file.getFolderHash());
                upload.setSaveType(file.getSaveType());
                fileMapper.insert(upload);
                return upload;
            }
        }
        return null;
    }

    @Override
    public File update(FileParam file) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getId, file.getId());
        wrapper.eq(File::getDriveId, SayaUtils.getDriveId());
        fileMapper.update(file.convertTo(), wrapper);
        return fileMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteBy(String fileHash) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getHash, fileHash);
        fileMapper.delete(wrapper);
    }

    @Override
    public File getById(Integer id) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getId, id);
        wrapper.eq(File::getDriveId, SayaUtils.getDriveId());
        return fileMapper.selectOne(wrapper);
    }

    @Override
    public List<File> listByFolder(String folderHash) {
        folderService.hasFolder(folderHash);
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getFolderHash, folderHash);
        wrapper.eq(File::getDriveId, SayaUtils.getDriveId());
        return fileMapper.selectList(wrapper);
    }

}
