package com.tracejp.saya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SaYa-main
 * @author traceJP
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@MapperScan("com.tracejp.saya.mapper")
public class SayaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SayaApplication.class, args);
    }

}
