package com.tracejp.saya.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import com.tracejp.saya.utils.SayaUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 回收站
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cld_recyclebin")
@ApiModel(value="Recyclebin对象", description="回收站")
public class Recyclebin extends SuperEntity {


    @ApiModelProperty(value = "文件所属用户（外键）")
    private String driveId = SayaUtils.getDriveId();

    @ApiModelProperty(value = "文件或文件夹哈希（外键）")
    private String hashId;

    @ApiModelProperty(value = "类型（1文件 2文件夹）")
    private String hashType;


}
