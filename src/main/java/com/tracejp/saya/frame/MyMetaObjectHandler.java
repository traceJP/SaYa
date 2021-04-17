package com.tracejp.saya.frame;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>mybatis-plus自动填充处理器<p/>
 *
 * @author traceJP
 * @since 2021/4/16 16:53
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入记录自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 记录创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 删除标志
        this.strictInsertFill(metaObject, "delFlag", String.class, "0");
    }

    /**
     * 修改记录自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 记录修改时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

}
