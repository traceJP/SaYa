package com.tracejp.saya.model.support;

import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.params.base.InputConverter;
import lombok.Data;

import java.util.Map;

/**
 * <p>上传结果<p/>
 *
 * @author traceJP
 * @since 2021/4/23 10:54
 */
@Data
public class UploadResult implements InputConverter<UploadParam> {

    /**
     * 当前分片数
     */
    private Integer chunkNumber;

    /**
     * 其他保存参数
     */
    private Map<String, Object> otherParam;

}
