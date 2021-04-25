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
@TableName("cld_share")
@ApiModel(value="Share对象", description="共享文件实体类")
public class Share extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "文件所有者id（外键）")
    private String driveId;

    @ApiModelProperty(value = "文件或文件夹哈希（外键）")
    private String hashId;

    @ApiModelProperty(value = "链接文件类型（1文件 2文件夹）")
    private String hashType;

    @ApiModelProperty(value = "生成的共享链接")
    private String shareUrl;

    @ApiModelProperty(value = "共享密码")
    private String sharePassword;

    @ApiModelProperty(value = "链接过期时间")
    private LocalDateTime shareExpiry;

    @ApiModelProperty(value = "链接总访问次数")
    private Integer shareAccessTotal;

    @ApiModelProperty(value = "链接已访问次数")
    private Integer shareAccessUsed;


}
