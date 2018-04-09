package com.liuyang19900520.controller;

import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuyang on 2018/3/19
 */
@RestController
@Slf4j
@RequestMapping("/check")
public class CheckController {


    @RequestMapping("/jwt")
    public Object checkJwt() {
        return ResultVo.success(Messages.OK, "jwt checked success");
    }

    @RequiresPermissions("system:*")
    @RequestMapping("/admin")
    public Object checkAdmin() {
        System.out.println("success");

        return ResultVo.success(Messages.OK, "admin checked sucess");
    }
}
