package com.tracejp.saya.model.enums;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/14 18:58
 */
public enum UserStatusEnum implements ValueEnum<String> {

    /**
     * 正常
     */
    NORMAL("0"),

    /**
     * 停用
     */
    DEACTIVATE("1");

    private final String value;

    UserStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
