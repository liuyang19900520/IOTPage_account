package com.liuyang19900520.shiro.jwt;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.UserService;
import com.liuyang19900520.utils.CryptoUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by liuyang on 2018/3/16
 */
public class HmacRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;//账号服务(持久化服务)

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
        if (jge > 900000000) {// 十分钟之前的时间戳，这是有效期可以双方约定由参数传过来
            throw new AuthenticationException("数字摘要失效！！！");
        }
        // 此处可以添加查询数据库检查账号是否存在、是否被锁定、是否被禁用等等逻辑
        // 从token中获取用户名
        String userName = hmacToken.getClientKey();

        // 根据用户名查询数据库
        SysUser user = userService.findUserByAccount(userName);

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
        String clientKey = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 根据客户标识（可以是用户名、app id等等） 查询并设置角色
//        Set<String> roles = accountProvider.loadRoles(clientKey);
//        info.setRoles(roles);
        // 根据客户标识（可以是用户名、app id等等） 查询并设置权限
//        Set<String> permissions = accountProvider.loadPermissions(clientKey);
//        info.setStringPermissions(permissions);
        // 根据username查询角色
        info.setRoles(Sets.newHashSet("admin", "superadmin"));

        // 根据username查询权限
        info.setStringPermissions(Sets.newHashSet("system:*"));
        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof HmacToken;
    }

}
