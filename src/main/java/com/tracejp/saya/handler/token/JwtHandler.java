package com.tracejp.saya.handler.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tracejp.saya.model.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author traceJP
 * @date 2021/4/8 17:09
 */
@Component
public class JwtHandler {

    @Autowired
    private JwtProperties properties;

    /**
     * token-Claim属性
     */
    private static final String claimName = "drive";

    /**
     * 签发token
     * @param drive 用户uuid
     * @return token
     */
    public String getToken(String drive) {
        return JWT.create()
                .withClaim(claimName, drive)
                .withHeader(properties.getHeader())
                .withExpiresAt(new Date(System.currentTimeMillis() + properties.getExpired()))
                .sign(Algorithm.HMAC256(properties.getSign()))
                ;
    }

    /**
     * 验证token
     * @param token token
     * @exception JWTVerificationException 令牌验证失败时抛出异常
     *      否者静默返回
     */
    public void verifyToken(String token) throws JWTVerificationException {
        JWT.require(Algorithm.HMAC256(properties.getSign()))
                .build()
                .verify(token)
        ;
    }

    /**
     * 通过token获取driveId
     * @param token token
     * @return 用户uuid
     */
    public String getDrive(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getSign()))
                .build()
                .verify(token)
                .getClaims()
                .get(claimName)
                .asString()
                ;
    }

}
