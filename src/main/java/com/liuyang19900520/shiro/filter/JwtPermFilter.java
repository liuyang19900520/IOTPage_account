package com.liuyang19900520.shiro.filter;

import com.liuyang19900520.shiro.jwt.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by liuyang on 2018/3/18
 *
 * @author liuya
 */
@Slf4j
public class JwtPermFilter extends JwtFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
                                      Object mappedValue) throws Exception {


        Subject subject = getSubject(request, response);

        if ((null == subject || !subject.isAuthenticated()) && isJwtSubmission(request)) {

            AuthenticationToken token = createToken(request, response);
            try {
                subject = getSubject(request, response);
                subject.login(token);
                boolean xxx = this.checkPerms(subject, mappedValue);
                return xxx;
            } catch (AuthenticationException e) {
                log.error(request.getRemoteHost()+" HMAC鉴权  "+e.getMessage());

            }
//
//            String[] perms = (String[]) mappedValue;
//            boolean isPermitted = true;
//            if (perms != null && perms.length > 0) {
//                if (perms.length == 1) {
//                    if (!subject.isPermitted(perms[0])) {
//                        isPermitted = false;
//                    }
//                } else {
//                    if (!subject.isPermittedAll(perms)) {
//                        isPermitted = false;
//                    }
//                }
//                return isPermitted;
//            }
        }

        return false;

    }

}
