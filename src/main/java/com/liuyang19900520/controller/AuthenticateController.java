package com.liuyang19900520.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import com.liuyang19900520.domain.SysUser;
import com.liuyang19900520.service.AuthenticateService;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.utils.CryptoUtil;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyang on 2018/3/16
 *
 * @author liuya
 */
@Slf4j

@RestController
@RequestMapping("/auth")
public class AuthenticateController {

    @Autowired
    AuthenticateService authenticateService;

    @Autowired
    private DefaultKaptcha captchaProducer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping(value = "/captcha")
    public Map<String, String> captcha() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String capText = captchaProducer.createText();
            String uuid = UUID.randomUUID().toString();
            redisTemplate.boundValueOps(uuid).set(capText, 60, TimeUnit.SECONDS);
            BufferedImage bi = captchaProducer.createImage(capText);
            ImageIO.write(bi, "png", baos);
            String imgBase64 = Base64.encodeBase64String(baos.toByteArray());
            return ImmutableMap.of(uuid, "data:image/jpeg;base64," + imgBase64);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 登录
     *
     * @param loginUser
     * @return
     */
    @ApiResponses({@ApiResponse(code = 200, message = "请求成功", response = SysUser.class)})
    @ApiOperation(value = "system login")
    @ApiParam(name = "Authorization", type = "header")
    @PostMapping("/login")
    public ResultVo login(@RequestBody LoginUser loginUser, Device device) {

        Set<String> roles = authenticateService.listRolesByAccount(loginUser.getUsername());
        Set<String> permissions = authenticateService.listPermissionsByAccount(loginUser.getUsername());

        StringBuffer strRoles = new StringBuffer();
        StringBuffer strPerms = new StringBuffer();

        permissions.stream().forEachOrdered(s -> strPerms.append(s).append(","));
        String permsJwt = strPerms.substring(0, strPerms.length() - 1);


        roles.stream().forEachOrdered(s -> strRoles.append(s).append(","));
        String rolesJwt = strRoles.substring(0, strRoles.length() - 1);

        String jwt = CryptoUtil.issueJwt(UUID.randomUUID().toString(), loginUser.getUsername(),
                rolesJwt, permsJwt, new Date(), CryptoUtil.ACCESS_TOKEN_TYPE);

        String refresh = CryptoUtil.issueJwt(UUID.randomUUID().toString(), loginUser.getUsername(),
                rolesJwt, permsJwt, new Date(), CryptoUtil.REFRESH_TOKEN_TYPE);

        HashMap<String, String> tokens = Maps.newHashMap();
        tokens.put("token", jwt);
        tokens.put("refreshToken", refresh);

        SysUser sysUser = new SysUser();
        sysUser.setToken(jwt);
        sysUser.setRefreshToken(refresh);


        return ResultVo.success(Messages.OK, sysUser);
    }


    @PostMapping("/logout")
    public ResultVo logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = CryptoUtil.parserToken(token);
        redisTemplate.boundValueOps(claims.getSubject()).set(token, 30 * 60 * 1000L);
        return ResultVo.success(Messages.OK, null);
    }

    @PostMapping("/regist/email")
    public Object register(@RequestBody SysUser user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            return authenticateService.regist("email", user);
        }
        if (StringUtils.isNotBlank(user.getMobilePhone())) {
            return authenticateService.regist("mobile", user);
        }
        return ResultVo.error(Messages.OK, "failed");
    }

    @GetMapping("/active")
    public Object active(HttpServletRequest request) {
        String code = request.getParameter("code");
        authenticateService.active(code);
        return ResultVo.success(Messages.OK, null);
    }

    @PostMapping("/message")
    public ResultVo sendMessage(HttpServletRequest request) {
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        //初始化clnt,使用单例方式
        YunpianClient clnt = new YunpianClient("9add2180ecb503f6cf3da38b147b49e1").init();

        //发送短信API
        Map<String, String> param = clnt.newParam(2);
        param.put(YunpianClient.MOBILE, "15943040340");
        param.put(YunpianClient.TEXT, "【東京IAIA】您的验证码是" + code + "。如非本人操作，请忽略本短信");
        Result<SmsSingleSend> r = clnt.sms().single_send(param);
        if (r.getCode() == 0) {
            redisTemplate.boundValueOps(String.valueOf(code)).set(code, 300, TimeUnit.SECONDS);
            return ResultVo.success(Messages.OK, null);
        }
//获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()

//账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*

//释放clnt
        clnt.close();
        return ResultVo.error(Messages.MESSAGE_ERROR, r.getDetail());
    }

}
