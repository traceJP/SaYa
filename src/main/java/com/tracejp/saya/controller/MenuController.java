package com.tracejp.saya.controller;

import com.tracejp.saya.model.entity.Menu;
import com.tracejp.saya.model.enums.AuthRoleEnum;
import com.tracejp.saya.model.enums.ValueEnum;
import com.tracejp.saya.service.MenuService;
import com.tracejp.saya.utils.SayaUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation("根据角色获取对应菜单信息")
    @GetMapping("/list")
    public List<Menu> roleMenu() {
        AuthRoleEnum role = ValueEnum.valueToEnum(AuthRoleEnum.class, SayaUtils.getUserByShiro().getUserType());
        return menuService.listMenu(role);
    }

}

