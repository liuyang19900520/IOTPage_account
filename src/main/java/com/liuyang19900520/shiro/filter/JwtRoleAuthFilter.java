package com.liuyang19900520.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by liuyang on 2018/3/18
 *
 * @author liuya
 */
@Slf4j
public class JwtRoleAuthFilter extends StatelessFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {


        Subject subject = getSubject(request, response);

        if ((null == subject || !subject.isAuthenticated()) && isJwtSubmission(request)) {

            AuthenticationToken token = createJwtToken(request, response);
            try {
                subject = getSubject(request, response);
                subject.login(token);
                boolean xxx = this.checkRoles(subject, mappedValue);
                return xxx;
            } catch (AuthenticationException e) {
                log.error(request.getRemoteHost() + " HMAC鉴权  " + e.getMessage());

            }
        }

        return false;

    }

}
