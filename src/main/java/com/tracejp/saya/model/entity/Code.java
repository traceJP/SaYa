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
 * 
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_code")
@ApiModel(value="Code对象", description="")
public class Code extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "激活码")
    private String codeContent;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime codeExpiry;

    @ApiModelProperty(value = "激活码使用总次数")
    private Integer codeAccessTotal;

    @ApiModelProperty(value = "激活码已使用次数")
    private Integer codeAccessUsed;

    @ApiModelProperty(value = "增加云盘空间字节数")
    private Long incrCloud;

    @ApiModelProperty(value = "增加下载用量字节数")
    private Long incrCdn;


}
