package com.liuyang19900520.shiro.filter;

import com.google.common.collect.Maps;
import com.liuyang19900520.commons.interceptor.LHttpServletRequestWrapper;
import com.liuyang19900520.commons.interceptor.LRequestJsonUtils;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.shiro.jwt.HmacToken;
import com.liuyang19900520.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liuyang on 2018/3/16
 */
public class HmacFilter extends AccessControlFilter {
    private static final Logger log = LoggerFactory.getLogger(AccessControlFilter.class);

    public static final String DEFAULT_CLIENTKEY_PARAM = "username";
    public static final String DEFAULT_TIMESTAMP_PARAM = "timeStamp";
    public static final String DEFAUL_DIGEST_PARAM = "digest";

    /**
     * 是否放行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {
        if (null != getSubject(request, response)
                && getSubject(request, response).isAuthenticated()) {
            return true;//已经认证过直接放行
        }
        return false;//转到拒绝访问处理逻辑
    }

    /**
     * 拒绝处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        if (isHmacSubmission(request)) {//如果是Hmac鉴权的请求
            //创建令牌
            AuthenticationToken token = createToken(request, response);
            try {
                Subject subject = getSubject(request, response);
                subject.login(token);//认证
                return true;//认证成功，过滤器链继续
            } catch (AuthenticationException e) {//认证失败，发送401状态并附带异常信息
                log.error(e.getMessage(), e);
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
        }
        return false;//打住，访问到此为止
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
        parameters.put("loginUser",loginUser);
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
        return (request instanceof HttpServletRequest)
                && StringUtils.isNotBlank(clientKey)
                && StringUtils.isNotBlank(timeStamp)
                && StringUtils.isNotBlank(digest);
    }

}
