package com.tracejp.saya.model.dto;

import cn.hutool.core.util.DesensitizedUtil;
import com.tracejp.saya.model.dto.base.OutputConverter;
import com.tracejp.saya.model.entity.User;
import lombok.Data;

/**
 * @author traceJP
 * @date 2021/4/10 20:07
 */
@Data
public class UserDto implements OutputConverter<UserDto, User> {

    private String driveId;

    private String type;

    private String name;

    private String email;

    private String phone;

    private String sex;

    private String avatar;

    /**
     * 当前账号是否存在密码
     */
    private Boolean hasPassword;

    /**
     * 去除敏感信息
     */
    public void removeSensitive() {
        phone = DesensitizedUtil.fixedPhone(phone);
    }

}
