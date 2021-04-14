package com.tracejp.saya.model.enums;

import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * <p>值枚举接口<p/>
 *
 * @author traceJP
 * @since 2021/4/14 19:06
 */
public interface ValueEnum<T> {

    /**
     * 将值转化为相应的枚举
     * @param enumType 枚举类
     * @param value    枚举值
     * @param <V>      通用枚举值
     * @param <E>      通用枚举类
     * @return 对应的枚举
     */
    static <V, E extends ValueEnum<V>> E valueToEnum(Class<E> enumType, V value) {
        Assert.notNull(enumType, "enum type must not be null");
        Assert.notNull(value, "value must not be null");
        Assert.isTrue(enumType.isEnum(), "type must be an enum type");

        return Stream.of(enumType.getEnumConstants())
                .filter(item -> item.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown database value: " + value));
    }

    /**
     * 获取枚举的值
     * @return 枚举值
     */
    T getValue();

}
