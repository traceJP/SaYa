package com.tracejp.saya.model.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户容量实体
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_volume")
@ApiModel(value="Volume对象", description="保存用户存储容量使用容量的实体")
public class Volume extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    @ApiModelProperty(value = "所属用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "云盘总容量")
    private Long volumeCloudTotal;

    @ApiModelProperty(value = "云盘已使用的容量")
    private Long volumeCloudUsed;

    @ApiModelProperty(value = "云盘下载总量（字节）")
    private Long volumeCdnTotal;

    @ApiModelProperty(value = "云盘已下载总量（字节）")
    private Long volumeCdnUsed;


}
