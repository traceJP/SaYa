package com.tracejp.saya.controller;


import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 注册用户实体 前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("通过driveId获取用户基本信息")
    @GetMapping("/get")
    public BaseResponse<UserDto> getUser(String drive) {
        UserDto userDto = new UserDto().convertFrom(userService.getByDrive(drive));
        return BaseResponse.ok(userDto);
    }

    @ApiOperation("修改用户基本信息")
    @PutMapping("/update/info")
    public BaseResponse<UserDto> updateUser(@RequestBody User user) {
        return null;
    }

    @ApiOperation("修改用户手机号")
    @PutMapping("/update/phone")
    public BaseResponse<?> updateUserPhone(Integer phone, String code) {
        return null;
    }

}

