package com.tracejp.saya.model.enums;

/**
 * <p>文件处理器枚举<p/>
 *
 * @author traceJP
 * @since 2021/4/21 20:34
 */
public enum AttachmentType implements ValueEnum<String> {

    /**
     * 本地
     */
    LOCAL("1"),

    /**
     * 阿里云oss
     */
    ALIOSS("2");

    private final String value;

    AttachmentType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
