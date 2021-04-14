package com.tracejp.saya.controller.open;

import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author traceJP
 * @date 2021/4/8 16:58
 */
@RestController()
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserService userService;

    @ApiOperation("通过手机和密码登录")
    @GetMapping("/pwd")
    public BaseResponse<UserDto> loginByPassword(String phone, String password) {
        return userService.authenticateByPassword(phone, password);
    }

    @ApiOperation("通过手机和短信验证码登录")
    @GetMapping("/sms")
    public BaseResponse<UserDto> loginBySms(String phone, String code) {
        return userService.authenticateBySms(phone, code);
    }

    @ApiOperation("获取短信验证码")
    @GetMapping("/get")
    public BaseResponse<?> getSms(String phone) {
        return userService.getAuthenticateSms(phone);
    }

}
