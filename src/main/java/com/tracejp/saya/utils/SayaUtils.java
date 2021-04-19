package com.tracejp.saya.utils;

import com.tracejp.saya.exception.ServiceException;
import com.tracejp.saya.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * <p>纱耶项目业务工具类<p/>
 *
 * @author traceJP
 * @since 2021/4/16 16:11
 */
@Slf4j
public class SayaUtils {

    /**
     * 获取由shiro保存的用户实体
     * @return 用户实体
     * @exception ServiceException 获取失败抛出异常
     */
    public static User getUserByShiro() {
        Subject subject = SecurityUtils.getSubject();
        Object obj = subject.getPrincipal();
        if (obj instanceof User) {
            return (User) obj;
        } else {
            throw new ServiceException("获取user实体失败");
        }
    }

    /**
     * 获取由shiro保存的用户uuid
     * @return 用户driveId
     */
    public static String getDriveId() {
        return getUserByShiro().getDriveId();
    }

    /**
     * 对单条记录的操作影响条数判断
     * @param result 影响条数
     * @exception ServiceException 影响条数不存在或为0时抛出异常
     */
    public static void influence(Integer result) {
        if (result == null || result == 0) {
            log.warn("数据库单条数据操作出现错误");
            throw new ServiceException("数据库增删改失败");
        }
    }



}
