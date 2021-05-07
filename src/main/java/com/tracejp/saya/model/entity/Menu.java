package com.tracejp.saya.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@ApiModel(value="Menu对象", description="")
public class Menu extends SuperEntity {


    @ApiModelProperty(value = "首级菜单项名")
    private String name;

    @ApiModelProperty(value = "菜单类型（m目录 f按钮）")
    private String type;

    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "父菜单id")
    private Integer parentId;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "备注")
    private String remake;


}
