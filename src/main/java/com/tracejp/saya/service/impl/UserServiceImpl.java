package com.tracejp.saya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.frame.JwtManager;
import com.tracejp.saya.frame.shiro.SmsToken;
import com.tracejp.saya.handler.file.AvatarHandler;
import com.tracejp.saya.handler.sms.AliSmsManager;
import com.tracejp.saya.handler.sms.SmsHandler;
import com.tracejp.saya.mapper.UserMapper;
import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.params.UserParam;
import com.tracejp.saya.service.UserService;
import com.tracejp.saya.utils.RegexUtils;
import com.tracejp.saya.utils.SayaUtils;
import com.tracejp.saya.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;


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

    @Autowired
    private AvatarHandler avatarHandler;

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
    public Optional<UserDto> authenticateByPassword(String phone, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(String.valueOf(phone), password);
        subject.login(token);
        User user = (User) subject.getPrincipal();
        return Optional.of(loginSuccess(user));
    }

    @Override
    public Optional<UserDto> authenticateBySms(String phone, String smsCode) {
        Subject subject = SecurityUtils.getSubject();
        SmsToken token = new SmsToken(phone, smsCode);
        subject.login(token);
        User user = (User) subject.getPrincipal();
        return Optional.of(loginSuccess(user));
    }

    @Override
    public void getSms(String phone, String template) {
        if (!RegexUtils.isPhone(phone)) {
            throw new ServiceException("手机号参数错误");
        }
        String ip = ServletUtils.getRequestIp();
        log.info("请求发送登录短信：IP为{}; 手机号为{}", ip, phone);
        // 发送验证码
        String code = aliSmsManager.sendVerificationCode(phone, template);
        smsHandler.remember(phone, code);
    }

    @Override
    public User getByDrive(String drive) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getDriveId, drive);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Optional<UserDto> updateAssets(UserParam userParam, MultipartFile avatar) {
        User user = SayaUtils.getUserByShiro();
        boolean modified = false;

        // 用户名修改
        if (StringUtils.isNotBlank(userParam.getUserName())) {
            if (RegexUtils.isLengOut(userParam.getUserName(), 20)) {
                throw new ServiceException("用户名过长");
            }
            user.setUserName(userParam.getUserName());
            modified = true;
        }

        // email修改
        if (StringUtils.isNotBlank(userParam.getEmail())) {
            if (!RegexUtils.isEmail(userParam.getEmail())) {
                throw new ServiceException("email格式不正确");
            }
            user.setEmail(userParam.getEmail());
            modified = true;
        }

        // 性别修改
        if (StringUtils.isNotBlank(userParam.getSex())) {
            if (StringUtils.equals(userParam.getSex(), "0") ||
                    StringUtils.equals(userParam.getSex(), "1") ||
                    StringUtils.equals(userParam.getSex(), "2")) {
                throw new ServiceException("性别错误");
            }
            user.setSex(userParam.getSex());
            modified = true;
        }

        // 头像修改
        if (!avatar.isEmpty()) {
            // 保存头像文件到本地
            AvatarHandler.Result result = avatarHandler.checkAndSave(avatar);
            if (result.getSuccess()) {
                // 本地存在对应头像
                if (StringUtils.isNotEmpty(user.getAvatar())) {
                    avatarHandler.markDel(user.getAvatar());
                }
                user.setAvatar(result.getRelativePath());
                modified = true;
            }
        }

        // 用户信息修改
        if (modified) {
            userMapper.updateById(user);
        }
        return Optional.of(new UserDto().convertFrom(user));
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        // 如果当前账户有密码
        if (StringUtils.isEmpty(user.getPassword())) {
            if (!StringUtils.equals(user.getPassword(), oldPassword)) {
                throw new ServiceException("旧密码和新密码不符合");
            }
        }

        // 新密码校验：必须大于8位且必须要有字母+数字
        if (newPassword.length() > 8 && RegexUtils.isENG_NUM(newPassword)) {
            throw new ServiceException("新密码格式不符");
        }

        // 更新密码
        user.setPassword(newPassword);
        userMapper.updateById(user);
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
