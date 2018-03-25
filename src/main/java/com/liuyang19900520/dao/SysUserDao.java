package com.liuyang19900520.dao;

import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.domain.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by liuyang on 2018/3/15
 *
 * @author liuyang
 */
public interface SysUserDao {

    SysUser selectByAccount(@Param("userName") String userNmae);

    List<SysRole> listRolesByAccount(@Param("userName") String userNmae);

    List<SysResource> listPermissionsByAccount(@Param("roleId") String roleId);


}
