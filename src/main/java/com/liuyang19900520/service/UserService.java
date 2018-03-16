package com.liuyang19900520.service;

import com.liuyang19900520.domain.SysUser;

/**
 * Created by liuyang on 2018/3/16
 */
public interface UserService {

    SysUser findUserByAccount(String userName);
}
