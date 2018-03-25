package com.liuyang19900520.shiro;

import com.liuyang19900520.shiro.jwt.HmacToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by liuyang on 2018/3/16
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    // token:用户在页面输入的用户名密码，info代表从密码中得到的加数据
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 向下转型
        HmacToken usertoken = (HmacToken) token;

        // 将用户输入的原始密码加密
        // 注意token.getPassword()拿到的是一个char[]，不能直接用toString()，它底层实现不是我们想的直接字符串，只能强转
        // 用户名做为salt
            Object tokenCredentials = Encrypt.md5(usertoken.getCredentials().toString(), usertoken.getPrincipal().toString());

        //取得数据库中的密码数据
        Object accountCredentials = getCredentials(info);

        return equals(tokenCredentials, accountCredentials);

    }
}
