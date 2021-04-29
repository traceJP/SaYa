package com.tracejp.saya.model.params;

import com.tracejp.saya.model.entity.File;
import com.tracejp.saya.model.params.base.InputConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/27 16:40
 */
@Data
public class FileParam implements InputConverter<File> {

    private Long id;

    @ApiModelProperty(value = "文件所在文件夹哈希（外键）")
    private String folderHash;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "加星标志（0未加 1已加）")
    private String starredFlag;

}
