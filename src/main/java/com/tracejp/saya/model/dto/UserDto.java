package com.tracejp.saya.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author traceJP
 * @date 2021/4/10 20:07
 */
@Data
public class UserDto {

    private String token;

    private String driveId;

    private String userType;

    private String userName;

    private String email;

    private String phone;

    private String sex;

    private String avatar;

}
