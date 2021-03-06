package com.tracejp.saya.model.constant;

/**
 * <p>redis缓存key前缀常量类</p>
 * @author traceJP
 * @since 2021/4/13 16:02
 */
public abstract class RedisCacheKeys {

// redis-key

    /**
     * 短信验证码key
     */
    public static final String SMS_CAPTCHA_PREFIX = "sys::sms:";

    /**
     * 文件上传初始化key
     */
    public static final String FILE_INIT_PREFIX = "cld::fileinit:";

    /**
     * 文件上传key
     */
    public static final String FILE_UPLOAD_PREFIX = "cld::fileupload:";



// spring-cache

    /**
     * 用户信息缓存
     */
    public static final String USER_INFO_DOMAIN = "user";

    /**
     * 菜单信息缓存
     */
    public static final String USER_MENU_DOMAIN = "menu";


}
