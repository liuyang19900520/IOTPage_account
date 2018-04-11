package com.liuyang19900520.shiro;

import com.liuyang19900520.shiro.token.HmacToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 通过调用context.setSessionCreationEnabled(false)表示不创建会话，
 * 如果之后调用Subject.getSession()将抛出DisabledSessionException异常。
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        AuthenticationToken token = context.getAuthenticationToken();
        if ((token instanceof HmacToken)) {
            // 当token为HmacToken时， 不创建 session
            context.setSessionCreationEnabled(false);
        }
        return super.createSubject(context);
    }


}
