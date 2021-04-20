package com.tracejp.saya.frame;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>mybatis-plus自动填充处理器<p/>
 *
 * @author traceJP
 * @since 2021/4/16 16:53
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * mybatis-plus自动注入
     * @return MetaObjectHandler
     */
    @Bean
    public MetaObjectHandler myMetaObjectHandler() {
         return new MetaObjectHandler() {
            /**
             * 插入记录自动填充
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                // 记录创建时间
                this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now());
                // 删除标志
                this.strictInsertFill(metaObject, "delFlag", String.class, "0");
            }

            /**
             * 修改记录自动填充
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                // 记录修改时间
                this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
            }

             /**
              * 填充模式：覆盖已有值的字段
              */
             @Override
             public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
                 Object obj = fieldVal.get();
                 if (Objects.nonNull(obj)) {
                     metaObject.setValue(fieldName, obj);
                 }
                 return this;
             }

         };
    }

    }
