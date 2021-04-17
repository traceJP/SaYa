package com.tracejp.saya.model.dto.base;

import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;

/**
 * <p>转换为DTO输出类接口<p/>
 * <b>实现类型必须等于DTO类型</b>
 * @author traceJP
 * @since 2021/4/14 19:33
 */
public interface OutputConverter<DTO extends OutputConverter<DTO, DOMAIN>, DOMAIN> {

    /**
     * 将域转换为dto，浅拷贝
     * @param domain 域数据
     * @return 拷贝类
     */
    @SuppressWarnings("unchecked")
    @NonNull
    default <T extends DTO> T convertFrom(@NonNull DOMAIN domain) {

        BeanUtils.copyProperties(domain, this);

        return (T) this;

    }

}
