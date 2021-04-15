package com.tracejp.saya.controller.open;

import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author traceJP
 * @date 2021/4/8 16:58
 */
@Api("公用系统登录接口")
@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;

    @ApiOperation("通过手机和密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "用户手机号"),
            @ApiImplicitParam(name = "password", value = "用户密码")
    })
    @GetMapping("/pwd")
    public BaseResponse<UserDto> loginByPassword(String phone, String password) {
        return userService.authenticateByPassword(phone, password);
    }

    @ApiOperation("通过手机和短信验证码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "用户手机号"),
            @ApiImplicitParam(name = "code", value = "短信验证码")
    })
    @GetMapping("/sms")
    public BaseResponse<UserDto> loginBySms(String phone, String code) {
        return userService.authenticateBySms(phone, code);
    }

    @ApiOperation("获取短信验证码")
    @ApiImplicitParam(name = "phone", value = "用户手机号")
    @GetMapping("/get")
    public BaseResponse<?> getSms(String phone) {
        return userService.getAuthenticateSms(phone);
    }

}
