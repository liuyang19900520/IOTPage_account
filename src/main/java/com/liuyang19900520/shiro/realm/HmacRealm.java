package com.liuyang19900520.shiro.realm;

import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.AuthenticateService;
import com.liuyang19900520.shiro.token.HmacToken;
import com.liuyang19900520.utils.CryptoUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by liuyang on 2018/3/16
 *
 * @author liuya
 */
public class HmacRealm extends AuthorizingRealm {

    private static final int EXPIRE_TIME = 600000;

    /**
     * 认证与权限服务
     */
    @Autowired
    private AuthenticateService authenticateService;

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        HmacToken hmacToken = (HmacToken) token;

        StringBuffer baseString = new StringBuffer();

        baseString.append(hmacToken.getClientKey()).append(hmacToken.getTimeStamp());
        //认证端生成摘要
        String serverDigest = CryptoUtil.hmacDigest(baseString.toString());

        //客户端请求的摘要和服务端生成的摘要不同
        if (!serverDigest.equals(hmacToken.getDigest())) {
            throw new AuthenticationException("数字摘要验证失败！！！");
        }
        Long visitTimeStamp = Long.valueOf(hmacToken.getTimeStamp());
        Long nowTimeStamp = System.currentTimeMillis();
        Long jge = nowTimeStamp - visitTimeStamp;
        // 十分钟之前的时间戳，这是有效期可以双方约定由参数传过来
        if (jge > EXPIRE_TIME) {
            throw new AuthenticationException("数字摘要失效！！！");
        }
        // 此处可以添加查询数据库检查账号是否存在、是否被锁定、是否被禁用等等逻辑
        // 从token中获取用户名
        String userName = hmacToken.getClientKey();

        // 根据用户名查询数据库
        SysUser user = authenticateService.findUserByAccount(userName);

        // 用户不存在
        if (user == null) {
            throw new UnknownAccountException();
        }

        // 用户被禁用
        if (user.getStatus() == 1) {
            throw new LockedAccountException();
        }

        try {
            return new SimpleAuthenticationInfo(
                    userName,
                    user.getPassword(),
                    getName()
            );
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }

    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String payload = (String) principals.getPrimaryPrincipal();
        if (payload.startsWith("hmac:") && payload.charAt(5) == '{'
                && payload.charAt(payload.length() - 1) == '}') {
            String appId = payload.substring(6,payload.length() - 1);
            SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
            Set<String> roles = this.authenticateService.listRolesByAccount(appId);
            Set<String> permissions = this.authenticateService.listPermissionsByAccount(appId);
            if(null!=roles&&!roles.isEmpty()) {
                info.setRoles(roles);
            }
            if(null!=permissions&&!permissions.isEmpty()) {
                info.setStringPermissions(permissions);
            }
            return info;
        }
//        String clientKey = (String) principals.getPrimaryPrincipal();
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//
//        // 根据客户标识（可以是用户名、app id等等） 查询并设置角色
//        Set<String> roles = authenticateService.listRolesByAccount(clientKey);
//        info.setRoles(roles);
//
//        // 根据客户标识（可以是用户名、app id等等） 查询并设置权限
//        Set<String> permissions = authenticateService.listPermissionsByAccount(clientKey);
//        info.setStringPermissions(permissions);

        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof HmacToken;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

}
