package com.tracejp.saya.model.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户日志记录实体
 * </p>
 *
 * @author TraceJP
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_log")
@ApiModel(value="UserLog对象", description="记录用户行为")
public class UserLog extends SuperEntity {

    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    @ApiModelProperty(value = "所属用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "上传文件哈希")
    private String uploadFileHash;

    @ApiModelProperty(value = "下载文件哈希")
    private String downloadFileHash;

    @ApiModelProperty(value = "删除文件哈希")
    private String deleteFileHash;

    @ApiModelProperty(value = "创建文件夹哈希")
    private String createFolderHash;

    @ApiModelProperty(value = "删除文件夹哈希")
    private String deleteFolderHash;

}
