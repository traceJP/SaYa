package com.tracejp.saya.model.params.base;

import com.tracejp.saya.model.enums.OrderEnum;
import com.tracejp.saya.utils.SayaUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>基本查询条件<p/>
 *
 * @author traceJP
 * @since 2021/5/16 17:19
 */
@Data
public class BaseFileQuery {

    /**
     * 用户id
     */
    private String driveId = SayaUtils.getDriveId();

    /**
     * 是否查询全部
     */
    private Boolean isAll = false;

    /**
     * 查询记录数
     */
    private Integer limit = 100;

    /**
     * 按什么排序
     */
    private OrderEnum orderBy = OrderEnum.NAME;

    /**
     * 排序规则
     */
    private String orderDirection = "ASC";

    /**
     * 是否是升序
     * @return 升序返回true，降序返回false
     */
    public boolean isAsc() {
        return StringUtils.equals(orderDirection, "ASC");
    }

}
