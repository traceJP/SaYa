package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.handler.file.FileHandlerManager;
import com.tracejp.saya.mapper.FileMapper;
import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.params.FileParam;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.service.FileService;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.utils.SayaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FileHandlerManager fileHandler;


    @Override
    public Optional<File> upload(UploadParam param) {

        // 父文件夹检测处理
        folderService.hasFolder(param.getFolderHash());

        // 做上传检查,如流量计算，内存计算等  2021.4.27

        // 秒传处理
        File md5file = md5SecondPass(param);
        if (Objects.nonNull(md5file)) {
            return Optional.of(md5file);
        }

        // 文件上传
        File upload = fileHandler.doUpload(param);

        if (Objects.nonNull(upload)) {
            // 上传成功

            // 流量内存计算 2021.4.27

            fileMapper.insert(upload);
        }

        return Optional.ofNullable(upload);
    }

    @Override
    public void download(String fileHash) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getFileHash, fileHash);
        wrapper.eq(File::getDriveId, SayaUtils.getDriveId());
        Optional<File> file = Optional.of(fileMapper.selectOne(wrapper));
        fileHandler.doDownload(file.get());

        // 下载后计算  2021.4.27

    }

    @Override
    public File md5SecondPass(UploadParam param) {
        if (!param.getEnableChunk() || param.getChunkNumber() == 1) {
            LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(File::getFileMd5, param.getFileMd5());
            File file = fileMapper.selectOne(wrapper);

            // 存在相同md5值
            if (Objects.nonNull(file)) {
                // 关闭分片上传，以免调用文件处理器init方法
                param.setEnableChunk(false);
                File upload = fileHandler.builderTransportFile(param);
                // 原属性覆盖并保存
                upload.setFolderHash(file.getFolderHash());
                upload.setFileSaveType(file.getFileSaveType());
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
