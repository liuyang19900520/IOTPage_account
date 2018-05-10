package com.liuyang19900520.dao;

import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.domain.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by liuyang on 2018/3/15
 *
 * @author liuyang
 */
public interface SysUserDao {

    SysUser selectByAccount(@Param("userName") String userName);

    Set<String> listRolesByAccount(@Param("userName") String userName);

    Set<String> listPermissionsByAccount(@Param("userName") String userName);

    SysUser checkRegistUser(SysUser user);

    int insertUser(SysUser user);

    int activeUser(@Param("code") String code);


}
