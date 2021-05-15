package com.tracejp.saya.service.base.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tracejp.saya.service.base.BaseService;

/**
 * <p>业务层超级父类<p/>
 *
 * @author traceJP
 * @since 2021/5/15 12:11
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
}
