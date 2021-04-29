package com.tracejp.saya.controller;


import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.params.FolderParam;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.FolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Api("用户文件夹接口")
@RestController
@RequestMapping("/folder")
@CrossOrigin
public class FolderController {

    @Autowired
    private FolderService folderService;

    @ApiOperation("获取文件夹中的所有内容")
    @ApiImplicitParam(name = "folderHash", value = "文件夹哈希")
    @GetMapping("/list")
    public List<Object> quireAll(String folderHash) {
        return folderService.getAll(folderHash);
    }

    @ApiOperation("获取文件夹基本信息")
    @ApiImplicitParam(name = "folderHash", value = "文件夹哈希")
    @GetMapping("/get")
    public Folder quireFolderInfo(String folderHash) {
        return folderService.getByHash(folderHash).orElseThrow(
                () -> new NotFoundException("文件信息不存在"));
    }

    @ApiOperation("创建文件夹")
    @ApiImplicitParam(name = "folder", value = "文件夹名/父文件哈希/是否加星")
    @PostMapping("/create")
    public BaseResponse<Folder> createFolder(@RequestBody FolderParam folder) {
        return BaseResponse.ok("已成功创建文件夹", folderService.createFolder(folder));
    }

    @ApiOperation("修改文件夹基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folder", value = "文件夹名/父文件哈希/是否加星"),
            @ApiImplicitParam(name = "folderHash", value = "当前文件的哈希")
    })
    @PutMapping("/update")
    public BaseResponse<Folder> updateFolder(@RequestBody FolderParam folder, String folderHash) {
        return BaseResponse.ok(folderService.updateFolder(folder, folderHash));
    }

}

