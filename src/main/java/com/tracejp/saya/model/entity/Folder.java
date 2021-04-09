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
@TableName("cld_folder")
@ApiModel(value="Folder对象", description="")
public class Folder extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "文件所属用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "是否为根文件夹（0否 1是）")
    private String folderRoot;

    @ApiModelProperty(value = "文件夹新建id")
    private Integer folderCreateId;

    @ApiModelProperty(value = "文件夹名")
    private String folderName;

    @ApiModelProperty(value = "文件夹哈希")
    private String folderHash;

    @ApiModelProperty(value = "文件夹父节点哈希")
    private String folderParentHash;

    @ApiModelProperty(value = "文件夹状态（0正常 1停用）")
    private String folderStatus;

    @ApiModelProperty(value = "加星标志（0未加 1已加）")
    private String starredFlag;


}
