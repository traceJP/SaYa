package com.tracejp.saya.frame;

import com.tracejp.saya.model.properties.Swagger2Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author traceJP
 * @date 2021/4/7 21:04
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Autowired
    Swagger2Properties properties;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(properties.getEnable())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                properties.getTitle(),
                properties.getDescription(),
                properties.getVersion(),
                properties.getServiceUrl(),
                null,
                null,
                null,
                new ArrayList<>()
        );
    }

}
