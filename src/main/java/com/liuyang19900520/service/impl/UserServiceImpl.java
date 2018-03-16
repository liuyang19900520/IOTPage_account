package com.liuyang19900520.service.impl;

import com.liuyang19900520.dao.SysUserDao;
import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuyang on 2018/3/16
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public SysUser findUserByAccount(String userName) {
        return sysUserDao.selectByAccount(userName);
    }


}
