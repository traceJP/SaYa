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
@TableName("cld_upload_info")
@ApiModel(value="UploadInfo对象", description="")
public class UploadInfo extends SuperEntity {


    @ApiModelProperty(value = "上传id（外键）")
    private Integer uploadId;

    @ApiModelProperty(value = "上传类型（1文件 2文件夹）")
    private String uploadType;

    @ApiModelProperty(value = "创建者ip地址")
    private String uploadIp;


}
