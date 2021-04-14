package com.tracejp.saya.utils;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * <p><p/>
 *
 * @author traceJP
 * @since 2021/4/14 15:23
 */
public class ServletUtils {

    /**
     * 获取当前响应
     * @return 可空的http响应
     */
    @NonNull
    public static Optional<HttpServletResponse> getCurrentResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> requestAttributes instanceof ServletRequestAttributes)
                .map(requestAttributes -> (ServletRequestAttributes) requestAttributes)
                .map(ServletRequestAttributes::getResponse);
    }

    /**
     * 获取当前请求
     * @return 可空的http请求
     */
    @NonNull
    public static Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> requestAttributes instanceof ServletRequestAttributes)
                .map(requestAttributes -> (ServletRequestAttributes) requestAttributes)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 获取当前请求ip
     * @return ip地址或为空
     */
    @Nullable
    public static String getRequestIp() {
        return getCurrentRequest().map(ServletUtil::getClientIP).orElse(null);
    }

    /**
     * 设置响应的Header
     * @param name key
     * @param value value
     * @return 设置成功返回true
     */
    public static boolean setCurrentHeader(String name, Object value) {
        if (getCurrentResponse().isPresent()) {
            ServletUtil.setHeader(getCurrentResponse().get(), name, value);
            return true;
        } else {
            return false;
        }
    }

}
