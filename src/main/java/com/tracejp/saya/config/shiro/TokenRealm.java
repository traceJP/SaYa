package com.tracejp.saya.config.shiro;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tracejp.saya.config.JwtManager;
import com.tracejp.saya.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author traceJP
 * @date 2021/4/8 19:26
 */
@Slf4j
@Component
public class TokenRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtManager jwtManager;

    /**
     * 当前传入shiro封装令牌类型
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // token验证
        String jwt = (String) token.getCredentials();
        // 异常转换
        try {
            jwtManager.verifyToken(jwt);
        } catch (TokenExpiredException e) {
            throw new AuthenticationException("Token已过期");
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("token验证失败");
        }

        String driveId = jwtManager.getDrive(jwt);
        // TODO: 2021/4/8 查表，判断token用户是否被封禁等其他操作

        return new SimpleAuthenticationInfo(driveId, jwt, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {


        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // TODO: 2021/4/8 通过数据库查询权限信息

        return info;
    }

}
