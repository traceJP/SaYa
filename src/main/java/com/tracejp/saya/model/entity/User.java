package com.tracejp.saya.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 注册用户实体
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@ApiModel(value="User对象", description="注册用户实体")
public class User extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "用户uuid")
    private String driveId;

    @ApiModelProperty(value = "用户类型（00系统用户 01注册用户）")
    private String type;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "用户手机号码（唯一）")
    private String phone;

    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private String sex;

    @ApiModelProperty(value = "用户头像路径")
    private String avatar;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "账号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "用户最后登录ip")
    private String loginIp;

    @ApiModelProperty(value = "用户最后登录时间")
    private LocalDateTime loginDate;


}
