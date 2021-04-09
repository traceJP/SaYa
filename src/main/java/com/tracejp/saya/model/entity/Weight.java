package com.tracejp.saya.model.entity;


import java.math.BigDecimal;
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
@TableName("sys_weight")
@ApiModel(value="Weight对象", description="")
public class Weight extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "所属用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "用户下载权重")
    private BigDecimal weightDownload;

    @ApiModelProperty(value = "用户下载的字节总数")
    private String downloadTotal;

    @ApiModelProperty(value = "用户上传的字节总数")
    private String uploadTotal;

    @ApiModelProperty(value = "用户登录总天数")
    private Integer loginTotal;

    @ApiModelProperty(value = "用户连续登录天数")
    private Integer loginContinuou;


}
