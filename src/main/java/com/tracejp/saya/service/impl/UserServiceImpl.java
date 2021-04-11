package com.tracejp.saya.service.impl;

import com.tracejp.saya.model.dto.UserDto;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.support.BaseResponse;
import com.tracejp.saya.service.UserService;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

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

    @Override
    public BaseResponse<UserDto> register(User user) {
        return null;
    }

    @Override
    public BaseResponse<UserDto> authenticateByPassword(Integer phone, String password) {
        Subject subject = SecurityUtils.getSubject();

        return null;
    }

    @Override
    public BaseResponse<UserDto> authenticateBySms(Integer phone, String smsCode) {
        return null;
    }

    @Override
    public BaseResponse<?> getAuthenticateSms(Integer phone, HttpServletRequest request) {
        return null;
    }

    @Override
    public User queryUserByDrive(String drive) {
        return null;
    }

    @Override
    public User queryUserByPhone(Integer phone) {
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
