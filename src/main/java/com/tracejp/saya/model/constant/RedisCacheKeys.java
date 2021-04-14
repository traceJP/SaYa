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

    /**
     * 短信手机痕迹key
     */
    public static final String SMS_PHONE_TRACE = "phone";

    /**
     * 短信ip痕迹key
     */
    public static final String SMS_IP_TRACE = "ip";

    /**
     * 短信黑名单key
     */
    public static final String SMS_BLACKLIST = "blacklist";






}
