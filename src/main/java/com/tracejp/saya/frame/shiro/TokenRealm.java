package com.tracejp.saya.frame.shiro;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tracejp.saya.handler.token.JwtHandler;
import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.enums.BaseStatusEnum;
import com.tracejp.saya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author traceJP
 * @date 2021/4/8 19:26
 */
@Slf4j
@Component
public class TokenRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private JwtHandler jwtHandler;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwt = (String) token.getCredentials();
        // jwt异常转换为shiro异常
        try {
            jwtHandler.verifyToken(jwt);
        } catch (TokenExpiredException e) {
            throw new ExpiredCredentialsException();
        } catch (JWTVerificationException e) {
            throw new IncorrectCredentialsException();
        }

        String driveId = jwtHandler.getDrive(jwt);
        User user = userService.getByDrive(driveId);
        // 账号是否停用
        if (StringUtils.equals(BaseStatusEnum.DEACTIVATE.getValue(), user.getStatus())) {
            throw new DisabledAccountException();
        }

        return new SimpleAuthenticationInfo(user, jwt, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        String userType = user.getUserType();
        // 系统用户角色授权
        if (StringUtils.equals(userType, "00")) {
            info.addRole("admin");
        }
        return info;
    }

}
