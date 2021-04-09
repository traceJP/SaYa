package com.tracejp.saya.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 本地文件存储
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cld_save_local")
@ApiModel(value="SaveLocal对象", description="本地文件存储")
public class SaveLocal extends SuperEntity {


    @ApiModelProperty(value = "文件哈希（外键）")
    private String fileHash;

    @ApiModelProperty(value = "本地文件路径")
    private String savePath;


}
