package com.tracejp.saya.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author traceJP
 * @date 2021/4/7 21:07
 */
@Component
@Data
@ConfigurationProperties("swagger2")
public class Swagger2Properties {

    /**
     * 是否开启swagger
     */
    private Boolean enable = true;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String description;

    /**
     * 版本
     */
    private String version = "0.0.1";

    /**
     * 服务条款url
     */
    private String serviceUrl;

    /**
     * 接口扫描包路径
     */
    private String basePackage;

}
