package com.tracejp.saya.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tracejp.saya.model.entity.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("cld_file")
@ApiModel(value="File对象", description="文件对象实体类")
public class File extends SuperEntity {


    @ApiModelProperty(value = "删除标志（0存在 1删除）")
    private String delFlag;

    @ApiModelProperty(value = "用户id（外键）")
    private String driveId;

    @ApiModelProperty(value = "文件所在文件夹哈希（外键）")
    private String folderHash;

    @ApiModelProperty(value = "文件上传id")
    private String fileUploadId;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "文件MD5")
    private String fileMd5;

    @ApiModelProperty(value = "文件哈希")
    private String fileHash;

    @ApiModelProperty(value = "文件大小（字节）")
    private String fileSize;

    @ApiModelProperty(value = "文件扩展名")
    private String fileExtension;

    @ApiModelProperty(value = "文件存储类型（1本地 2oss）")
    private String fileSaveType;

    @ApiModelProperty(value = "文件状态（0正常 1停用）")
    private String fileStatus;

    @ApiModelProperty(value = "加星标志（0未加 1已加）")
    private String starredFlag;


}
