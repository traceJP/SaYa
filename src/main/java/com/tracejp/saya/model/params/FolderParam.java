package com.tracejp.saya.model.params;

import com.tracejp.saya.model.entity.Folder;
import com.tracejp.saya.model.params.base.InputConverter;
import lombok.Data;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/16 15:26
 */
@Data
public class FolderParam implements InputConverter<Folder> {

    private String folderName;

    private String folderParentHash;

    private String starredFlag;

}
