package com.liuyang19900520.shiro.jwt;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by liuyang on 2018/3/18
 * @author liuya
 */
@Data
public class JwtToken implements AuthenticationToken {

    private String jwt;// json web token
    private String host;// 客户端IP


    public JwtToken(String jwt, String host) {
        this.jwt = jwt;
        this.host = host;
    }

    @Override
    public Object getPrincipal() {
        return this.jwt;
    }

    @Override
    public Object getCredentials() {
        return Boolean.TRUE;
    }

}
