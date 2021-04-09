package com.tracejp.saya.model.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author traceJP
 * @date 2021/4/6 20:54
 */
@Data
public class SuperEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
