package com.liuyang19900520.service.impl;

import com.liuyang19900520.dao.SysUserDao;
import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.AuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by liuyang on 2018/3/16
 */
@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public SysUser findUserByAccount(String userName) {
        return sysUserDao.selectByAccount(userName);
    }

    @Override
    public Set<String> listRolesByAccount(String userName) {

        return sysUserDao.listRolesByAccount(userName);
    }

    @Override
    public Set<String> listPermissionsByAccount(String userName) {
        return sysUserDao.listPermissionsByAccount(userName);
    }




}
