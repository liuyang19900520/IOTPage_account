package com.liuyang19900520.dao;

import com.liuyang19900520.domain.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * Created by liuyang on 2018/3/15
 */
public interface SysUserDao {

    SysUser selectByAccount(@Param("userName") String userNmae);

}
