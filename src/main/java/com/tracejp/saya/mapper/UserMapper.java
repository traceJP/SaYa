package com.tracejp.saya.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tracejp.saya.model.entity.User;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 注册用户实体 Mapper 接口
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

}
