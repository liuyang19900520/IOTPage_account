package com.liuyang19900520.controller;

import com.google.common.collect.Maps;
import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.service.UserService;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.utils.CryptoUtil;
import com.liuyang19900520.utils.SubjectUtil;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * Created by liuyang on 2018/3/16
 *
 * @author liuya
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticateController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Object applyToken(@RequestBody LoginUser loginUser) {

        List<SysRole> sysRoles = userService.listRolesByAccount(loginUser.getUsername());
        List<SysResource> sysResources = userService.listpermissionsByAccount(loginUser.getUsername());

        StringBuffer roles = new StringBuffer();
        StringBuffer permissions = new StringBuffer();
        for (int i = 0; i < sysRoles.size(); i++) {
            roles.append(sysRoles.get(i).getCode());
            if (i != sysRoles.size() - 1) {
                roles.append(",");
            }
        }
        for (int i = 0; i < sysResources.size(); i++) {
            permissions.append(sysResources.get(i).getPermission());
            if (i != sysResources.size() - 1) {
                permissions.append(",");
            }
        }

        String jwt = CryptoUtil.issueJwt(UUID.randomUUID().toString(), loginUser.getUsername(),
                roles.toString(), permissions.toString(), new Date());

        return ResultVo.success(Messages.OK, jwt);
    }


}
