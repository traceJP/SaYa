package com.tracejp.saya.frame.shiro;

import com.tracejp.saya.handler.sms.SmsHandler;
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
public class SmsRealm extends AuthorizingRealm {

    @Autowired
    private SmsHandler smsHandler;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof SmsToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SmsToken auth = (SmsToken) token;
        String phone = (String) auth.getPrincipal();
        String code = (String) auth.getCredentials();

        // 检查手机验证码是否正确
        if (!smsHandler.authenticate(phone, code)) {
            throw new IncorrectCredentialsException();
        }

        User user = userService.getByPhone(phone);

        // 是否是新用户
        if (user == null) {
            user = userService.register(phone);
        }

        // 账号是否停用
        if (StringUtils.equals(BaseStatusEnum.DEACTIVATE.getValue(), user.getStatus())) {
            throw new DisabledAccountException();
        }

        return new SimpleAuthenticationInfo(user, code, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

}
