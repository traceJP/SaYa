package com.tracejp.saya.frame.shiro;

import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.service.UserService;
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
        Integer phone = Integer.valueOf(tokenInfo.getUsername());
        String password = String.valueOf(tokenInfo.getPassword());

        User user = userService.queryUserByPhone(phone);
        if(user == null) {
            throw new AuthenticationException("账户不存在");
        }
        if(!password.equals(user.getPassword())) {
            throw new AuthenticationException("密码错误");
        }

        return new SimpleAuthenticationInfo(user.getDriveId(), user.getPassword(), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

}
