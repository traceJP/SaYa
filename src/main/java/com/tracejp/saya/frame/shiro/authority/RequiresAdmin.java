package com.tracejp.saya.frame.shiro.authority;

import org.apache.shiro.authz.annotation.RequiresRoles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>管理员权限注解<p/>
 *
 * @author traceJP
 * @since 2021/4/30 23:22
 */
@RequiresRoles(value = "admin")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAdmin {

}
