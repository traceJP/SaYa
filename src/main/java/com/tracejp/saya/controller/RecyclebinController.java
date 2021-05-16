package com.tracejp.saya.controller;

import com.tracejp.saya.model.entity.Recyclebin;
import com.tracejp.saya.model.params.base.BaseFileQuery;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.RecyclebinService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 回收站 前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@RestController
@RequestMapping("/recyclebin")
public class RecyclebinController {

    @Autowired
    private RecyclebinService recyclebinService;


    @ApiOperation("向回收站添加一个文件或文件夹")
    @ApiImplicitParam(name = "recyclebin", value = "回收站实体")
    @PostMapping("/trash")
    public BaseResponse<?> trash(@RequestBody Recyclebin recyclebin) {
        recyclebinService.createBy(recyclebin);
        return BaseResponse.ok("已将文件移动到回收站");
    }

    @ApiOperation("向回收站移除一个文件或文件夹")
    @ApiImplicitParam(name = "recyclebin", value = "回收站实体")
    @DeleteMapping("/restore")
    public BaseResponse<?> restore(@RequestBody Recyclebin recyclebin) {
        recyclebinService.deleteBy(recyclebin.getHashType(), recyclebin.getHashId());
        return BaseResponse.ok("文件已还原");
    }

    @ApiOperation("通过查询参数查询回收站中所有内容")
    @ApiImplicitParam(name = "query", value = "查询条件")
    @PostMapping("/list")
    public List<Object> listByFolder(@RequestBody BaseFileQuery query) {
        return recyclebinService.listOfFolderContent(query);
    }

}

