package com.tracejp.saya.model.params;

import com.tracejp.saya.model.params.base.BaseFileQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>文件夹全部内容查询参数<p/>
 *
 * @author traceJP
 * @since 2021/5/15 18:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderAllQuery extends BaseFileQuery {

    /**
     * 文件夹id
     */
    private String folderHash = "root";

}
