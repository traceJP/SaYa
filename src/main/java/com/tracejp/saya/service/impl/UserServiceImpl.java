package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tracejp.saya.frame.JwtManager;
import com.tracejp.saya.frame.shiro.SmsToken;
import com.tracejp.saya.handler.sms.AliSmsManager;
import com.tracejp.saya.handler.sms.SmsHandler;
import com.tracejp.saya.mapper.UserMapper;
import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.support.BadResponse;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import com.tracejp.saya.utils.ServletUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 注册用户实体 服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private AliSmsManager aliSmsManager;

    @Autowired
    private SmsHandler smsHandler;

    @Override
    public User register(String phone) {

        // 初始化

        return null;
    }

    @Override
    public BaseResponse<UserDto> authenticateByPassword(String phone, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(String.valueOf(phone), password);
        try {
            subject.login(token);
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

        User user = (User) subject.getPrincipal();
        UserDto userDto = new UserDto().convertFrom(user);

        // 更新用户登录记录
        loginUpdate(user.getDriveId());

        // 签发token
        ServletUtils.setCurrentHeader(HEADER_TOKEN_NAME, jwtManager.getToken(user.getDriveId()));

        String msg = "登录成功";
        return BaseResponse.ok(msg, userDto);
    }

    @Override
    public BaseResponse<UserDto> authenticateBySms(String phone, String smsCode) {
        Subject subject = SecurityUtils.getSubject();
        SmsToken token = new SmsToken(phone, smsCode);
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
             return BadResponse.bad("验证码不正确");
        }

        return null;
    }

    @Override
    public BaseResponse<?> getAuthenticateSms(String phone) {



        return null;
    }

    @Override
    public void loginUpdate(String drive) {
        UpdateWrapper<User> condition = new UpdateWrapper<User>().eq("drive", drive);
        User user = new User();
        user.setLoginIp(ServletUtils.getRequestIp());
        user.setLoginDate(LocalDateTime.now());
        userMapper.update(user, condition);
    }

    @Override
    public User queryAllByDrive(String drive) {
        return null;
    }

    @Override
    public User queryAllByPhone(String phone) {
        return null;
    }

    @Override
    public BaseResponse<UserDto> updateAssets() {
        return null;
    }

    @Override
    public BaseResponse<?> updatePassword() {
        return null;
    }

    @Override
    public BaseResponse<?> updatePhone() {
        return null;
    }

}
