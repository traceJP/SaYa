package com.tracejp.saya.service;

import com.tracejp.saya.model.entity.Menu;
import com.tracejp.saya.model.enums.AuthRoleEnum;
import com.tracejp.saya.service.base.BaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
public interface MenuService extends BaseService<Menu> {

    /**
     * 通过角色标识符获取菜单
     * @param role AuthRoleEnum
     * @return List<Menu>
     */
    List<Menu> listMenu(AuthRoleEnum role);

}
