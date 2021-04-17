package com.tracejp.saya.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/16 21:32
 */
public enum YesNoStrEnum implements ValueEnum<String> {

    /**
     * 是
     */
    YES("1"),

    /**
     * 否
     */
    NO("0");

    private final String value;

    YesNoStrEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 判断传入值是否是该枚举中的内容
     * @param val 传入值
     * @return 是返回true，否则false
     */
    public static boolean isInclude(String val) {
        return StringUtils.equals(val, YES.value) || StringUtils.equals(val, NO.value);
    }

    public static boolean isNotInclude(String val) {
        return !isInclude(val);
    }

}
