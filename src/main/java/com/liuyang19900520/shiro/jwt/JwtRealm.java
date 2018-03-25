package com.liuyang19900520.shiro.jwt;

import com.liuyang19900520.utils.CryptoUtil;
import io.jsonwebtoken.*;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;

import java.util.Map;
import java.util.Set;

/**
 * Created by liuyang on 2018/3/18
 */
public class JwtRealm extends AuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof JwtToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        // 只认证JwtToken
        if (!(token instanceof JwtToken)) {
            return null;
        }
        String verifyToken = "";
        String jwt = ((JwtToken) token).getJwt();

        try {
            // 预先解析Payload
            // 没有做任何的签名校验
            verifyToken = CryptoUtil.verifyToken(jwt);


        } catch (MalformedJwtException e) {
            throw new AuthenticationException("jwt格式错误");
        } catch (Exception e) {
            throw new AuthenticationException("jwt无效");
        }

        return new SimpleAuthenticationInfo(verifyToken, Boolean.TRUE, this.getName());
    }

    /**
     * 授权,JWT已包含访问主张只需要解析其中的主张定义就行了
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String payload = (String) principals.getPrimaryPrincipal();
//        // likely to be json, parse it:
//        if (payload.startsWith("jwt:") && payload.charAt(4) == '{'
//                && payload.charAt(payload.length() - 1) == '}') {
//
//            Map<String, Object> payloadMap = CryptoUtil.readValue(payload.substring(4));
//            Set<String> roles = CryptoUtil.split((String) payloadMap.get("roles"));
//            Set<String> permissions = CryptoUtil.split((String) payloadMap.get("perms"));
//            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//            if (null != roles && !roles.isEmpty()) {
//                info.setRoles(roles);
//            }
//            if (null != permissions && !permissions.isEmpty()) {
//                info.setStringPermissions(permissions);
//            }
//            return info;
//        }
        return null;
    }


}
