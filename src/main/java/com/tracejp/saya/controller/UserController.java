package com.tracejp.saya.controller;

import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.params.UserParam;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import com.tracejp.saya.utils.SayaUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 注册用户实体 前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("通过driveId获取用户基本信息")
    @GetMapping("/get")
    public UserDto getUser() {
        String drive = SayaUtils.getDriveId();
        return new UserDto().convertFrom(userService.getByDrive(drive));
    }

    @ApiOperation("修改用户基本信息")
    @PutMapping("/update/info")
    public BaseResponse<UserDto> updateUser(UserParam userParam, @RequestPart(required = false) MultipartFile avatar) {
        UserDto userDto = userService.updateAssets(userParam, avatar).orElseThrow(
                () -> new NotFoundException("未找到用户信息"));
        return BaseResponse.ok(userDto);
    }

    @ApiOperation("修改用户密码")
    @PutMapping("/update/pwd")
    public BaseResponse<?> updatePassword(String oldPassword, String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return BaseResponse.ok("重置密码成功");
    }

}

