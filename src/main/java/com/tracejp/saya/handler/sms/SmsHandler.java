package com.tracejp.saya.handler.sms;

import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.utils.RedisUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * <p>短信缓存验证类<p/>
 * @author traceJP
 * @since 2021/4/12 14:48
 */
@Component
public class SmsHandler {

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 短信缓存前缀
     * sys::sms:{phone}
     */
    private static final String CACHE_KEY_PREFIX = RedisCacheKeys.SYSTEM_DOMAIN + RedisCacheKeys.DOMAIN +
            RedisCacheKeys.SMS_CAPTCHA + RedisCacheKeys.PACK;

    /**
     * 将目标手机号，验证码保存到redis中
     * @param phone 目标手机号
     * @param code 验证码
     * @return 保存成功返回true，否则返回false
     */
    public boolean remember(String phone, String code) {
        CacheBuilder entity = new CacheBuilder(phone, code);
        return entity.saveCache();
    }

    /**
     * 通过手机号，验证码 进行认证
     * @param phone 手机号
     * @param code 验证码
     * @return 认证成功返回true，否则返回false
     */
    public boolean authenticate(String phone, String code) {
        String key = CACHE_KEY_PREFIX + phone;
        CacheBuilder cache = (CacheBuilder) redisUtils.get(key);
        CacheBuilder auth = new CacheBuilder(phone, code);
        return auth.equals(cache);
    }


    /**
     * 构建短信缓存实体类
     * 仅允许有参构造
     */
    @AllArgsConstructor
    protected final class CacheBuilder implements Serializable {

        /**
         * 缓存生命周期（秒）
         */
        private static final long CACHE_KEY_TIME = 60 * 5;

        /**
         * 手机号
         */
        private final String phone;

        /**
         * 验证码
         */
        private final String code;

        /**
         * 保存实体到缓存中
         * @return 保存成功返回true，否则返回false
         */
        public boolean saveCache() {
            String key = CACHE_KEY_PREFIX + phone;
            return redisUtils.set(key, this, CACHE_KEY_TIME);
        }

        /**
         * 获取当前key的存活时间
         * @return 存活时间
         */
        public long getSurviveTime() {
            return CACHE_KEY_TIME - redisUtils.getExpire(phone);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheBuilder)) {
                return false;
            }
            CacheBuilder cache = (CacheBuilder) obj;
            return StringUtils.equals(cache.code, code) && StringUtils.equals(cache.phone, phone);
        }

    }

}
