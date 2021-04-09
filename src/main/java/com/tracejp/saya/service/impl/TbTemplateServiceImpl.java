package com.tracejp.saya.service.impl;


import com.tracejp.saya.mapper.TbTemplateMapper;
import com.tracejp.saya.model.entity.TbTemplate;
import com.tracejp.saya.service.TbTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class TbTemplateServiceImpl implements TbTemplateService {

    @Autowired
    TbTemplateMapper mapper;

    @Override
    public void helloService() {
        System.out.println("HelloWorld");
        TbTemplate tb = new TbTemplate();
        tb.setGmtCreate(LocalDateTime.now());
        tb.setGmtModified(LocalDateTime.now());
        tb.setDelFlag("1");
        mapper.insert(tb);
    }
}
