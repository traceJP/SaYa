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
@TableName("sys_volume")
@ApiModel(value="Volume对象", description="")
public class Volume extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "所属用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "云盘总容量")
    private String volumeCloudTotal;

    @ApiModelProperty(value = "云盘已使用的容量")
    private String volumeCloudUsed;

    @ApiModelProperty(value = "云盘下载总量（字节）")
    private String volumeCdnTotal;

    @ApiModelProperty(value = "云盘已下载总量（字节）")
    private String volumeCdnUsed;


}
