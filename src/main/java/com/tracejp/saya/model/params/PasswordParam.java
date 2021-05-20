package com.tracejp.saya.model.params;

import lombok.Data;

/**
 * <p>修改密码参数<p/>
 *
 * @author traceJP
 * @since 2021/5/20 10:54
 */
@Data
public class PasswordParam {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

}
