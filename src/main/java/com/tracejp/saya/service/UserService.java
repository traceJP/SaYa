package com.tracejp.saya.service;

import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.params.UserParam;

import java.util.Optional;

/**
 * <p>
 * 注册用户实体 服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface UserService {

    /**
     * Token响应头key
     */
    String HEADER_TOKEN_NAME = "token";

    /**
     * 登录短信模板
     */
    String SMS_LOGIN_TEMPLATE = "login";

    /**
     * 用户注册
     * @param phone 手机号
     * @return
     */
    User register(String phone);

    /**
     * 手机密码登录认证
     * @param phone 手机号
     * @param password 密码
     * @return
     */
    Optional<UserDto> authenticateByPassword(String phone, String password);

    /**
     * 手机验证码登录认证
     * @param phone 手机号
     * @param smsCode 短信验证码
     * @return
     */
    Optional<UserDto> authenticateBySms(String phone, String smsCode);

    /**
     * 获取登录认证短信
     * @param phone 手机号
     * @return
     */
    void getAuthenticateSms(String phone);

    /**
     * 通过driveId获取用户信息
     * @param drive 用户uuid
     * @return User
     */
    User queryAllByDrive(String drive);

    /**
     * 通过手机号获取用户信息
     * @param phone 手机号
     * @return User
     */
    User queryAllByPhone(String phone);

    /**
     * 修改用户基本信息
     * @return
     */
    Optional<UserDto> updateAssets(UserParam userParam);

    /**
     * 修改用户密码
     * @return
     */
    void updatePassword(String oldPassword, String newPassword);

    /**
     * 修改手机号
     * @return
     */
    void updatePhone(String newPhone, String code);

}
