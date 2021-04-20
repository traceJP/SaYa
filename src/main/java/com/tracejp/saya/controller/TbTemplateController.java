package com.tracejp.saya.controller;


import com.tracejp.saya.handler.token.JwtHandler;
import com.tracejp.saya.service.TbTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@RestController
@RequestMapping("/tbTemplate")
public class TbTemplateController {

    @Autowired
    TbTemplateService service;

    @GetMapping("/hello")
    public String hello() {
        service.helloService();
        return "ok";
    }

    @Autowired
    JwtHandler manager;

    @GetMapping("/getToken")
    public String getToken() {
        return manager.getToken("123");
    }

}

