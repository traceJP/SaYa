package com.tracejp.saya.model.enums;

import lombok.AllArgsConstructor;

/**
 * <p>shiro权限角色枚举<p/>
 *
 * @author traceJP
 * @since 2021/5/2 15:01
 */
@AllArgsConstructor
public enum AuthRoleEnum implements ValueEnum<String> {

    /**
     * 系统用户
     */
    SYSTEM("00", "admin"),

    /**
     * 注册用户
     */
    REGISTER("01", "user");

    /**
     * 数据库值
     */
    private final String value;

    /**
     * shiro角色权限标识
     */
    private final String role;

    @Override
    public String getValue() {
        return value;
    }

    public String getRole() {
        return role;
    }

}
