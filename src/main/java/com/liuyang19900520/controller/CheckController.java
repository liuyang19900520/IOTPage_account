package com.liuyang19900520.controller;

import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuyang on 2018/3/19
 * @author liuya
 */
@RestController
@Slf4j
@RequestMapping("/check")
public class CheckController {


    @PostMapping("/jwt")
    public Object checkJwt() {
        return ResultVo.success(Messages.OK, "token checked success");
    }

    @PostMapping("/roles/admin")
    public Object checkAdmin() {
        System.out.println("success");
        return ResultVo.success(Messages.OK, "admin checked sucess");
    }

    @PostMapping("/roles/super")
    public Object checkSuper() {
        System.out.println("success");
        return ResultVo.success(Messages.OK, "admin checked sucess");
    }
}
