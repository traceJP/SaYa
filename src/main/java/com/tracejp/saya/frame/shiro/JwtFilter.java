package com.tracejp.saya.frame.shiro;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author traceJP
 * @date 2021/4/7 21:59
 * 自定义shiro过滤器：仅判断请求中是否存在token，如果存在则调用主题login，无论如何都进行放行。
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 携带token的请求头key
     */
    private static final String headerName = "token";

    /**
     * 检查请求头是否存在token
     * 存在则进行登录尝试，否则默认拦截
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(headerName);

        // token为空时直接拦截
        if (StringUtil.isNullOrEmpty(token)) {
            responseError(request, response, "Token为空，请登录后尝试");
            return false;
        }

        // 封装token为shiro令牌进行登录尝试
        JwtToken jwtToken = new JwtToken(token);
        try {
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (ExpiredCredentialsException e) {
            responseError(request, response, "Token过期，请重新登录");
        } catch (IncorrectCredentialsException e) {
            responseError(request, response, "Token错误，请输入正确的凭证");
        } catch (Exception e) {
            responseError(request, response, "已被JwtFilter拦截，具体异常未捕获");
        }
        return false;
    }

    /**
     * 对ajax跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 将非法请求跳转至 /unauthorized
     *  - 使用重定向时会被shiro底层覆盖
     */
    private void responseError(ServletRequest request, ServletResponse response, String message) {
        try {
            message = URLEncoder.encode(message, "UTF-8");
            request.getRequestDispatcher("/unauthorized/" + message).forward(request, response);
        } catch (Exception e) {
            log.error("可能为shiro过滤器重定向/unauthorized失败或字符编码未找到");
        }
    }

}
