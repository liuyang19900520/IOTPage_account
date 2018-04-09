package com.liuyang19900520.shiro.filter;

import com.google.common.collect.Maps;
import com.liuyang19900520.commons.interceptor.LHttpServletRequestWrapper;
import com.liuyang19900520.commons.interceptor.LRequestJsonUtils;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.shiro.jwt.HmacToken;
import com.liuyang19900520.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liuyang on 2018/3/16
 *
 * @author liuya
 */
@Slf4j
public class HmacFilter extends AccessControlFilter {

    /**
     * 是否放行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {
        if (null != getSubject(request, response)
                && getSubject(request, response).isAuthenticated()) {
            //已经认证过直接放行
            return true;
        }
        //转到拒绝访问处理逻辑
        return false;
    }

    /**
     * 拒绝处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        //如果是Hmac鉴权的请求
        if (isHmacSubmission(request)) {
            //创建令牌
            AuthenticationToken token = createToken(request, response);
            try {
                Subject subject = getSubject(request, response);
                //认证
                subject.login(token);
                if (subject.isAuthenticated()) {
                    return true;
                }
            } catch (UnknownAccountException exception) {
                exception.printStackTrace();
                log.info("账号不存在");
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            } catch (IncorrectCredentialsException exception) {
                exception.printStackTrace();
                log.info("错误的凭证，用户名或密码不正确");
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            } catch (LockedAccountException exception) {
                exception.printStackTrace();
                log.info("账户已锁定");
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            } catch (ExcessiveAttemptsException exception) {
                exception.printStackTrace();
                log.info("错误次数过多");
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            } catch (AuthenticationException exception) {
                exception.printStackTrace();
                log.info("认证失败");
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            }

        }
        //打住，访问到此为止
        return false;
    }

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        LHttpServletRequestWrapper myWrapper = new LHttpServletRequestWrapper(req);
        String jsonStr = LRequestJsonUtils.getRequestJsonString(myWrapper);
        LoginUser loginUser = JsonUtils.jsonToPojo(jsonStr, LoginUser.class);
        String clientKey = loginUser.getUsername();
        String timeStamp = req.getHeader("Date");
        String digest = req.getHeader("Authorization");
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("loginUser", loginUser);
        String host = request.getRemoteHost();
        return new HmacToken(clientKey, timeStamp, digest, host, parameters);
    }

    protected boolean isHmacSubmission(ServletRequest request) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        LHttpServletRequestWrapper myWrapper = new LHttpServletRequestWrapper(req);
        String jsonStr = LRequestJsonUtils.getRequestJsonString(myWrapper);
        LoginUser loginUser = JsonUtils.jsonToPojo(jsonStr, LoginUser.class);
        String clientKey = loginUser.getUsername();
        String timeStamp = req.getHeader("Date");
        String digest = req.getHeader("Authorization");
        String jwt = req.getHeader("token");
        return (request instanceof HttpServletRequest)
                && StringUtils.isNotBlank(clientKey)
                && StringUtils.isNotBlank(timeStamp)
                && StringUtils.isNotBlank(digest)
                && StringUtils.isBlank(jwt);
    }

}
