package com.tracejp.saya.model.dto;

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

    private String userType;

    private String userName;

    private String email;

    private String phone;

    private String sex;

    private String avatar;

}
