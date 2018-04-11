package com.liuyang19900520.shiro.token;

import com.liuyang19900520.shiro.LoginUser;
import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.Map;

/**
 * Created by liuyang on 2018/3/16
 */
@Data
public class HmacToken implements AuthenticationToken {

    private String clientKey;// 客户标识（可以是用户名、app id等等）
    private String digest;// 消息摘要
    private String timeStamp;// 时间戳
    private Map<String, Object> parameters;// 访问参数
    private String host;// 客户端IP

    public HmacToken(String clientKey, String timeStamp, String digest
            , String host, Map<String, Object> parameters) {
        this.clientKey = clientKey;
        this.timeStamp = timeStamp;
        this.digest = digest;
        this.host = host;
        this.parameters = parameters;

    }


    @Override
    public Object getPrincipal() {
        return ((LoginUser)parameters.get("loginUser")).getUsername();
    }

    @Override
    public Object getCredentials() {
        return ((LoginUser)parameters.get("loginUser")).getPassword();
    }
}
