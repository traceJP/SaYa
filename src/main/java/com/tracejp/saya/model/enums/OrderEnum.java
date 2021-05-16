package com.tracejp.saya.model.enums;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/5/15 19:05
 */
public enum OrderEnum implements ValueEnum<String> {

    /**
     * 名称
     */
    NAME("name"),

    /**
     * 创建时间
     */
    CREATE_TIME("gmt_create"),

    /**
     * 修改时间
     */
    MODIFY_TIME("gmt_modified"),

    /**
     * 文件大小
     */
    FILE_SIZE("size");


    private final String value;

    OrderEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
