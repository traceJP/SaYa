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
@TableName("cld_save_oss")
@ApiModel(value="SaveOss对象", description="")
public class SaveOss extends SuperEntity {


    @ApiModelProperty(value = "文件哈希（外键）")
    private String fileHash;

    @ApiModelProperty(value = "阿里主账号")
    private Integer ossAccess;

    @ApiModelProperty(value = "阿里oss容器名")
    private String ossBucket;

    @ApiModelProperty(value = "阿里oss容器地域")
    private String ossEndpoint;


}
