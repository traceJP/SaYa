package com.tracejp.saya.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.handler.token.JwtHandler;
import com.tracejp.saya.frame.shiro.SmsToken;
import com.tracejp.saya.handler.file.AvatarHandler;
import com.tracejp.saya.handler.sms.AliSmsManager;
import com.tracejp.saya.handler.sms.SmsHandler;
import com.tracejp.saya.mapper.UserMapper;
import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.enums.AuthRoleEnum;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.model.params.UserParam;
import com.tracejp.saya.service.FolderService;
import com.tracejp.saya.service.UserService;
import com.tracejp.saya.service.VolumeService;
import com.tracejp.saya.utils.RegexUtils;
import com.tracejp.saya.utils.SayaUtils;
import com.tracejp.saya.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;
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
    private FolderService folderService;

    @Autowired
    private VolumeService volumeService;

    @Autowired
    private JwtHandler jwtHandler;

    @Autowired
    private AliSmsManager aliSmsManager;

    @Autowired
    private SmsHandler smsHandler;

    @Autowired
    private AvatarHandler avatarHandler;

    @Override
    @Transactional
    public User register(String phone) {

        // 初始化sys_user表
        User user = new User();
        user.setDriveId(IdUtil.fastUUID());
        user.setType(AuthRoleEnum.REGISTER.getValue());
        user.setPhone(phone);
        user.setStatus(BaseStatusEnum.NORMAL.getValue());
        user.setLoginIp(ServletUtils.getRequestIp());
        user.setLoginDate(LocalDateTime.now());
        userMapper.insert(user);

        // 初始化folder
        folderService.createRoot(user.getDriveId());

        // 初始化volume
        volumeService.createByDefault(user.getDriveId());

        return user;
    }

    @Override
    public Optional<UserDto> authenticateByPassword(String phone, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(phone, password);
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
    @Cacheable(cacheNames = RedisCacheKeys.USER_INFO_DOMAIN)
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
    @CacheEvict(cacheNames = RedisCacheKeys.USER_INFO_DOMAIN, key = "T(com.tracejp.saya.utils.SayaUtils).driveId")
    public Optional<UserDto> updateAssets(UserParam userParam, MultipartFile avatar) {
        User user = SayaUtils.getUserByShiro();
        boolean modified = false;

        if (Objects.nonNull(userParam)) {
            // 用户名修改
            if (StringUtils.isNotBlank(userParam.getName())) {
                if (RegexUtils.isLengOut(userParam.getName(), 20)) {
                    throw new ServiceException("用户名过长");
                }
                user.setName(userParam.getName());
                modified = true;
            }

            // email修改
            if (StringUtils.isNotBlank(userParam.getEmail())) {
                if (RegexUtils.isEmail(userParam.getEmail())) {
                    user.setEmail(userParam.getEmail());
                    modified = true;
                } else {
                    throw new ServiceException("email格式不正确");
                }
            }

            // 性别修改
            if (StringUtils.isNotBlank(userParam.getSex())) {
                if (StringUtils.equals(userParam.getSex(), "0") ||
                        StringUtils.equals(userParam.getSex(), "1") ||
                        StringUtils.equals(userParam.getSex(), "2")) {
                    user.setSex(userParam.getSex());
                    modified = true;
                } else {
                    throw new ServiceException("性别错误");
                }
            }
        }

        // 头像修改
        if (Objects.nonNull(avatar) && !avatar.isEmpty()) {
            // 保存头像文件到本地
            AvatarHandler.Result result = avatarHandler.checkAndSave(avatar);
            if (result.getSuccess()) {
                // 本地存在对应头像
                if (StringUtils.isNotEmpty(user.getAvatar())) {
                    avatarHandler.markDel(user.getAvatar());
                }
                user.setAvatar(result.getRelativePath());
                modified = true;
            } else {
                throw new ServiceException("头像上传失败");
            }
        }

        // 用户信息修改
        if (modified) {
            SayaUtils.influence(userMapper.updateById(user));
        }
        return Optional.of(new UserDto().convertFrom(user));
    }

    @Override
    @CacheEvict(cacheNames = RedisCacheKeys.USER_INFO_DOMAIN, key = "T(com.tracejp.saya.utils.SayaUtils).driveId")
    public void updatePassword(String oldPassword, String newPassword) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        // 如果当前账户有密码
        if (StringUtils.isNotEmpty(user.getPassword())) {
            if (!StringUtils.equals(user.getPassword(), DigestUtil.md5Hex(oldPassword))) {
                throw new ServiceException("旧密码和新密码不符合");
            }
        }

        // 新密码校验：必须大于8位且必须要有字母+数字
        if (newPassword.length() < 8 && !RegexUtils.isENG_NUM(newPassword)) {
            throw new ServiceException("新密码格式不符");
        }

        // 更新密码
        user.setPassword(DigestUtil.md5Hex(newPassword));
        SayaUtils.influence(userMapper.updateById(user));
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
        ServletUtils.setCurrentHeader(HEADER_TOKEN_NAME, jwtHandler.getToken(user.getDriveId()));
        return userDto;
    }

}
