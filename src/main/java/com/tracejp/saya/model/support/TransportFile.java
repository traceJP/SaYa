package com.tracejp.saya.model.support;

import com.tracejp.saya.model.entity.File;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/25 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransportFile extends File {

    /**
     * 其他参数
     */
    private Map<String, Object> otherParam;

}
