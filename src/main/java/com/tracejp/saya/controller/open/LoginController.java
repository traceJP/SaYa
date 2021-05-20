package com.tracejp.saya.controller.open;

import com.tracejp.saya.exception.NotFoundException;
import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.params.LoginParam;
import com.tracejp.saya.model.support.BadResponse;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author traceJP
 * @date 2021/4/8 16:58
 */
@Api("公用系统登录接口")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @ApiOperation("通过手机和密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "用户手机号"),
            @ApiImplicitParam(name = "password", value = "用户密码")
    })
    @PostMapping("/pwd")
    public BaseResponse<UserDto> loginByPassword(@RequestBody LoginParam param) {
        try {
            Optional<UserDto> userDto = userService.authenticateByPassword(param.getPhone(), param.getPassword());
            return BaseResponse.ok(userDto.orElseThrow(() -> new NotFoundException("用户信息未找到")));
        } catch (UnknownAccountException e) {
            String msg = "未知的手机号";
            return BadResponse.bad(msg);
        } catch (IncorrectCredentialsException e) {
            String msg = "密码错误";
            return BadResponse.bad(msg);
        } catch (DisabledAccountException e) {
            String msg = "账号已被禁用，请联系管理员";
            return BadResponse.bad(msg);
        }
    }

    @ApiOperation("通过手机和短信验证码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "用户手机号"),
            @ApiImplicitParam(name = "code", value = "短信验证码")
    })
    @PostMapping("/sms")
    public BaseResponse<UserDto> loginBySms(@RequestBody LoginParam param) {
        try {
            Optional<UserDto> userDto = userService.authenticateBySms(param.getPhone(), param.getSmsCode());
            return BaseResponse.ok(userDto.orElseThrow(() -> new NotFoundException("用户信息未找到")));
        } catch (IncorrectCredentialsException e) {
            String msg = "验证码不正确";
            return BadResponse.bad(msg);
        } catch (DisabledAccountException e) {
            String msg = "账号已被禁用，请联系管理员";
            return BadResponse.bad(msg);
        }
    }

    @ApiOperation("获取短信验证码")
    @ApiImplicitParam(name = "phone", value = "用户手机号")
    @GetMapping("/get")
    public BaseResponse<?> getSms(String phone) {
        userService.getSms(phone, userService.SMS_LOGIN_TEMPLATE);
        return BaseResponse.ok("验证码已成功发送");
    }

}
