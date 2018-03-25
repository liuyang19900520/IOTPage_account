package com.liuyang19900520.controller;

import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuyang on 2018/3/19
 */
@RestController
@Slf4j
public class TestController {


    @RequestMapping("/api/delete")
    public Object delete() {
        log.debug("这就代表这个方法已经执行");
        return ResultVo.success(Messages.OK, "success");
    }
}
