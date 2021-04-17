package com.tracejp.saya.model.params.base;

import com.tracejp.saya.utils.BeanUtils;
import com.tracejp.saya.utils.ReflectionUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * <p>输入param转换为实体域接口<p/>
 * <b>实现类型必须等于param的类型</b>
 * @author traceJP
 * @since 2021/4/15 21:47
 */
public interface InputConverter<DOMAIN> {

    /**
     * 将param转换为域
     * @return 具有和param有相同值的新域（非空）
     */
    @SuppressWarnings("unchecked")
    default DOMAIN convertTo() {
        // 获取参数化类型
            //InputConverter.class, this.getClass()
        ParameterizedType currentType = parameterizedType();

        // 断言不为空
        Objects.requireNonNull(currentType, "无法获取实际类型，因为参数化类型为null");

        Class<DOMAIN> domainClass = (Class<DOMAIN>) currentType.getActualTypeArguments()[0];

        return BeanUtils.transformFrom(this, domainClass);

    }

    /**
     * 通过域更新param
     * @param domain 需要更新的域
     */
    default void update(DOMAIN domain) {
        BeanUtils.updateProperties(this, domain);
    }

    /**
     * 获取参数化类型
     * @return parameterized type or null
     */
    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtils.getParameterizedType(InputConverter.class, this.getClass());
    }

}
