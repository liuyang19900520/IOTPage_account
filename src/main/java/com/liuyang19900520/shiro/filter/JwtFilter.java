package com.liuyang19900520.shiro.filter;

import com.liuyang19900520.shiro.jwt.JwtToken;
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

/**
 * Created by liuyang on 2018/3/18
 *
 * @author liuya
 */
public class JwtFilter extends AccessControlFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessControlFilter.class);

    public static final String DEFAULT_JWT_PARAM = "token";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (null != getSubject(request, response)
                && getSubject(request, response).isAuthenticated()) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isJwtSubmission(request)) {
            AuthenticationToken token = createToken(request, response);
            try {
                Subject subject = getSubject(request, response);
                subject.login(token);
                return true;
            } catch (AuthenticationException e) {
                log.error(e.getMessage(), e);
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
        }
        return false;
    }

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String timeStamp = req.getHeader("Date");
        String digest = req.getHeader("Authorization");
        String jwt = req.getHeader("token");
        String host = request.getRemoteHost();
        log.info("authenticate jwt token:" + jwt);
        System.out.println("jwt:" + jwt);
        return new JwtToken(jwt, host);
    }

    protected boolean isJwtSubmission(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = req.getHeader("token");
        return (request instanceof HttpServletRequest)
                && StringUtils.isNotBlank(jwt);
    }


}