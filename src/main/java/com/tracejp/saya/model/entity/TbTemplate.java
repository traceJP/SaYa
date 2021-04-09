package com.tracejp.saya.model.entity;


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
@ApiModel(value="TbTemplate对象", description="")
public class TbTemplate extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;


}
