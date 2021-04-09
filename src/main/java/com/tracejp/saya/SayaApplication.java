package com.tracejp.saya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SaYa-main
 * @author traceJP
 */
@SpringBootApplication
@MapperScan("com.tracejp.saya.mapper")
public class SayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SayaApplication.class, args);
    }

}
