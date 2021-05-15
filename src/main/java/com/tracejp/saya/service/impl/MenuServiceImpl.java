package com.tracejp.saya.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tracejp.saya.mapper.MenuMapper;
import com.tracejp.saya.model.constant.RedisCacheKeys;
import com.tracejp.saya.model.entity.Menu;
import com.tracejp.saya.model.enums.AuthRoleEnum;
import com.tracejp.saya.service.MenuService;
import com.tracejp.saya.service.base.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    @Cacheable(cacheNames = RedisCacheKeys.USER_MENU_DOMAIN)
    public List<Menu> listMenu(AuthRoleEnum role) {
        LambdaQueryWrapper<Menu> wrapper = Wrappers.lambdaQuery();
        List<String> roles = new ArrayList<>(2);
        roles.add(role.getValue());
        if (role == AuthRoleEnum.SYSTEM) {
            roles.add(AuthRoleEnum.REGISTER.getValue());
        }
        wrapper.in(Menu::getPerms, roles);
        wrapper.orderByAsc(Menu::getOrderNum);
        return menuMapper.selectList(wrapper);
    }

}
