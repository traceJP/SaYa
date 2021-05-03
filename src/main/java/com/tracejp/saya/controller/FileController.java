package com.tracejp.saya.controller;


import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.params.FileParam;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Api("用户文件接口")
@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("用户文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "param", value = "上传文件附加信息，用于确认上传方式等"),
            @ApiImplicitParam(name = "file", value = "上传的文件内容")
    })
    @PostMapping("/upload")
    public BaseResponse<File> upload(UploadParam param, @RequestPart MultipartFile file) {
        param.setFile(file);
        Optional<File> result = fileService.upload(param);
        return result.map(value -> BaseResponse.ok("文件上传成功", value))
                .orElseGet(() -> BaseResponse.ok("当前分片已经上传成功"));
    }

    @ApiOperation("用户文件下载")
    @ApiImplicitParam(name = "fileHash", value = "目标下载文件哈希")
    @GetMapping("/download/{fileHash}")
    public void download(@PathVariable("fileHash") String fileHash) {
        fileService.download(fileHash);
    }

    @ApiOperation("文件基本信息修改")
    @ApiImplicitParam(name = "param", value = "需要修改的文件基本信息")
    @PutMapping("/update")
    public BaseResponse<File> update(FileParam param) {
        return BaseResponse.ok("文件信息修改成功", fileService.update(param));
    }

    @ApiOperation("用户文件删除")
    @ApiImplicitParam(name = "fileHash", value = "目标删除文件哈希")
    @DeleteMapping("/delete")
    public BaseResponse<?> delete(String fileHash) {
        fileService.deleteBy(fileHash);
        return BaseResponse.ok("文件已被永久移除");
    }

    @ApiOperation("根据文件id获取文件信息")
    @ApiImplicitParam(name = "id", value = "文件自增id")
    @GetMapping("/get/{id}")
    public File getBy(@PathVariable("id") Integer id) {
        return fileService.getById(id);
    }

}

