package com.tracejp.saya.frame;

import com.tracejp.saya.model.constant.RedisCacheKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>spring缓存配置<p/>
 *
 * @author traceJP
 * @since 2021/5/1 8:50
 */
@Configuration
@EnableCaching
public class SpringCacheConfig {

    /**
     * 默认缓存键时间 -> 1天
     */
    private final Duration defaultExpired = Duration.ofDays(1L);

    @Value("${jwt.token.expired}")
    private Integer tokenExpired;


    /**
     * 缓存键生命周期配置
     * @return Map
     */
    private Map<String, Duration> constantCacheMap() {
        Map<String, Duration> cacheMap = new LinkedHashMap<>();
        cacheMap.put(RedisCacheKeys.USER_INFO_DOMAIN, Duration.ofSeconds(tokenExpired));
        return cacheMap;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {

        // 获取redis连接工厂
        RedisCacheWriter redisCacheWriter = RedisCacheWriter
                .nonLockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));

        // 获取redis序列化配置
        RedisSerializationContext.SerializationPair<?> serializationPair = RedisSerializationContext
                .SerializationPair
                .fromSerializer(redisTemplate.getValueSerializer());

        // 默认键处理配置
        RedisCacheConfiguration defaultRedisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(defaultExpired)
                .serializeValuesWith(serializationPair);

        // 自定义键处理配置
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new LinkedHashMap<>();
        constantCacheMap().forEach((k, v) -> {
            RedisCacheConfiguration constantRedisCacheConfiguration = RedisCacheConfiguration
                    .defaultCacheConfig()
                    .entryTtl(v)
                    .serializeValuesWith(serializationPair);
            redisCacheConfigurationMap.put(k, constantRedisCacheConfiguration);
        });

        return new RedisCacheManager(redisCacheWriter, defaultRedisCacheConfiguration, redisCacheConfigurationMap);
    }

}
