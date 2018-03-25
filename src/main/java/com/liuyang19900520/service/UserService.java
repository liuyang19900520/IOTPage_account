package com.liuyang19900520.service;

import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.domain.SysUser;

import java.util.List;

/**
 * Created by liuyang on 2018/3/16
 * @author liuya
 */
public interface UserService {

    SysUser findUserByAccount(String userName);

    List<SysRole> listRolesByAccount(String userName);

    List<SysResource> listpermissionsByAccount(String userName);
}
