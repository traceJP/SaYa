package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.tracejp.saya.utils.RegexUtils;
import com.tracejp.saya.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
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
        // 初始化cld_folder表，为用户自动新建一个root文件夹

        // 初始化sys_user表，添加一个用户

        // 初始化sys_volume表，为用户设置云盘容量等

        // 初始化sys_weight表，为用户设置权重等

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
        String msg = "登录成功";
        return BaseResponse.ok(msg, loginSuccess(user));
    }

    @Override
    public BaseResponse<UserDto> authenticateBySms(String phone, String smsCode) {
        Subject subject = SecurityUtils.getSubject();
        SmsToken token = new SmsToken(phone, smsCode);
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            String msg = "验证码不正确";
             return BadResponse.bad(msg);
        } catch (DisabledAccountException e) {
            String msg = "账号已被禁用，请联系管理员";
            return BadResponse.bad(msg);
        }

        User user = (User) subject.getPrincipal();
        String msg = "登录成功";
        return BaseResponse.ok(msg, loginSuccess(user));
    }

    @Override
    public BaseResponse<?> getAuthenticateSms(String phone) {
        String ip = ServletUtils.getRequestIp();
        log.info("请求发送登录短信：IP为{}; 手机号为{}", ip, phone);
        // 发送验证码
        try {
            String code = aliSmsManager.sendVerificationCode(phone, SMS_LOGIN_TEMPLATE);
            smsHandler.remember(phone, code);
        } catch (Exception e) {
            log.warn("登录短信发送失败：IP为{}; 手机号为{}", ip, phone);
            String msg = "短信发送失败，请勿频繁发送";
            return BadResponse.bad(msg);
        }
        String msg = "短信发送成功";
        return BaseResponse.ok(msg);
    }

    @Override
    public User queryAllByDrive(String drive) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("drive", drive);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User queryAllByPhone(String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("phone", phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public BaseResponse<UserDto> updateAssets() {
        return null;
    }

    @Override
    public BaseResponse<?> updatePassword(String oldPassword, String newPassword) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        // 如果当前账户有密码
        if (StringUtils.isEmpty(user.getPassword())) {
            // 旧密码和新密码不符合
            if (!StringUtils.equals(user.getPassword(), oldPassword)) {
                String msg = "旧密码与原密码不匹配";
                return BadResponse.bad(msg);
            }
        }

        // 新密码校验：必须大于8位且必须要有字母+数字
        if (newPassword.length() > 8 && RegexUtils.isENG_NUM(newPassword)) {
            String msg = "新密码格式不符";
            return BadResponse.bad(msg);
        }

        // 更新密码
        user.setPassword(newPassword);
        userMapper.updateById(user);
        String msg = "更新密码成功";
        return BaseResponse.ok(msg);
    }

    @Override
    public BaseResponse<?> updatePhone(String newPhone, String code) {
        return null;
    }

    /**
     * 登录成功后的执行方法
     * @param user user实体
     * @return userDto
     */
    private UserDto loginSuccess(User user) {
        UserDto userDto = new UserDto().convertFrom(user);
        // 更新用户登录记录
        user.setLoginIp(ServletUtils.getRequestIp());
        user.setLoginDate(LocalDateTime.now());
        userMapper.updateById(user);
        // 签发token
        ServletUtils.setCurrentHeader(HEADER_TOKEN_NAME, jwtManager.getToken(user.getDriveId()));
        return userDto;
    }

}
