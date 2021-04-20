package com.tracejp.saya.frame.shiro;

import cn.hutool.crypto.digest.DigestUtil;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author traceJP
 * @date 2021/4/7 10:08
 */
@Component
public class PasswordRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken tokenInfo = (UsernamePasswordToken) token;
        String clientPhone = tokenInfo.getUsername();
        String clientPassword = DigestUtil.md5Hex(String.valueOf(tokenInfo.getPassword()));
        User user = userService.getByPhone(clientPhone);
        // 是否存在手机号
        if (user == null) {
            throw new UnknownAccountException();
        }
        // 密码是否正确
        if (!StringUtils.equals(clientPassword, user.getPassword())) {
            throw new IncorrectCredentialsException();
        }
        // 账号是否停用
        if (StringUtils.equals(BaseStatusEnum.DEACTIVATE.getValue(), user.getStatus())) {
            throw new DisabledAccountException();
        }

        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

}
