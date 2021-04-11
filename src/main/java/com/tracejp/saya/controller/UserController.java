package com.tracejp.saya.controller;


import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.support.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class UserController {

    @ApiOperation("通过driveId获取用户基本信息")
    @GetMapping("/get")
    public BaseResponse<UserDto> getUser(String drive) {
        return null;
    }

    @ApiOperation("获取验证码")
    @GetMapping("/code/get")
    public BaseResponse<?> getSmsCode() {
        return null;
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

