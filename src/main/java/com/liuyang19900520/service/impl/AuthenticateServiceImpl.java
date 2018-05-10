package com.liuyang19900520.service.impl;

import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import com.liuyang19900520.dao.SysUserDao;
import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.AuthenticateService;
import com.liuyang19900520.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyang on 2018/3/16
 */
@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

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

    @Override
    public Object regist(String type, SysUser user) {

        SysUser sysUser = sysUserDao.checkRegistUser(user);

        if (sysUser != null) {
            return ResultVo.error(Messages.USER_EXISTED, "注册的用户存在");
        }

        user.setCreateAt(new Date());
        user.setUpdateAt(new Date());
        user.setCreateBy(user.getUserName());
        user.setUpdateBy(user.getUserName());


        if (StringUtils.equals("mobile", type)) {
            user.setStatus(1);
            int i = sysUserDao.insertUser(user);
            if (i > 0) {
                return ResultVo.success(Messages.OK, "注册成功");
            }

        }
        if (StringUtils.equals("email", type)) {
            String code = UUID.randomUUID().toString().replace("-", "");
            user.setSignature(code);
            user.setStatus(0);
            int i = sysUserDao.insertUser(user);
            if (i > 0) {
                redisTemplate.boundValueOps(code).set(code, 10, TimeUnit.MINUTES);
                String to = user.getEmail();
                String subject = "【東京IAIA】 注册验证";
                String content = "来自【東京IAIA】" + "\n" + "亲爱的" + user.getUserName() + "\n" + "    欢迎您注册东京IOT AI 产业联盟，点击此链接激活，完成注册" + "\n" +
                        " http://localhost:8085/auth/active?code=" + code + "\n" + "如果不能调转，可将连接复制到浏览器中进行访问";

                Runnable emailRunnable = new Runnable() {
                    @Override
                    public void run() {
                        emailService.sendSimpleMessage(to, subject, content);
                    }
                };
                taskExecutor.execute(emailRunnable);

                return ResultVo.success(Messages.OK, "注册成功,请激活");
            }


        }

        return null;
    }

    @Override
    public Object active(String code) {
        if (redisTemplate.boundValueOps(code).get() != null) {
            int i = sysUserDao.activeUser(code);
        } else {
            return ResultVo.error(Messages.OK, "请重新激活");
        }

        return null;
    }


}
