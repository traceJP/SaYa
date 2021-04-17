package com.tracejp.saya.model.params;

import com.tracejp.saya.model.entity.User;
import com.tracejp.saya.model.params.base.InputConverter;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author traceJP
 * @date 2021/4/10 21:46
 */
@Data
public class UserParam implements InputConverter<User> {

    private String userName;

    private String email;

    private String sex;

    private MultipartFile avatar;

}
