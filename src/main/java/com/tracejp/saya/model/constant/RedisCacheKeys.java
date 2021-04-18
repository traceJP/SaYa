package com.tracejp.saya.model.constant;

/**
 * <p>redis缓存key前缀常量类</p>
 * @author traceJP
 * @since 2021/4/13 16:02
 */
public abstract class RedisCacheKeys {

    /**
     * 包分隔符
     */
    public static final String PACK = ":";

    /**
     * 域分隔符
     */
    public static final String DOMAIN = "::";

    /**
     * 用户模块域名
     */
    public static final String SYSTEM_DOMAIN = "sys";

    /**
     * 文件模块域名
     */
    public static final String CLOUD_DOMAIN = "cld";


// 短信缓存
    /**
     * 短信验证码key
     */
    public static final String SMS_CAPTCHA = "sms";


// 头像文件缓存
    /**
     * 头像删除标记key
     */
    public static final String AVATAR_DEL = "avatardel";







}
