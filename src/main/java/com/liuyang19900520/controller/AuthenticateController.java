package com.liuyang19900520.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import com.liuyang19900520.domain.SysResource;
import com.liuyang19900520.domain.SysRole;
import com.liuyang19900520.service.AuthenticateService;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.utils.CryptoUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
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
    @PostMapping("/login")
    public Object applyToken(@RequestBody LoginUser loginUser, Device device) {

        Set<String> roles = authenticateService.listRolesByAccount(loginUser.getUsername());
        Set<String> permissions = authenticateService.listPermissionsByAccount(loginUser.getUsername());

        String jwt = CryptoUtil.issueJwt(UUID.randomUUID().toString(), loginUser.getUsername(),
                roles.toString(), permissions.toString(), new Date(), CryptoUtil.ACCESS_TOKEN_TYPE);

        String refresh = CryptoUtil.issueJwt(UUID.randomUUID().toString(), loginUser.getUsername(),
                roles.toString(), permissions.toString(), new Date(), CryptoUtil.REFRESH_TOKEN_TYPE);

        HashMap<String, String> tokens = Maps.newHashMap();
        tokens.put("token", jwt);
        tokens.put("refreshToken", refresh);

        return ResultVo.success(Messages.OK, tokens);
    }


    @PostMapping
    public Object logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = CryptoUtil.parserToken(token);
        redisTemplate.boundValueOps(claims.getSubject()).set(token, 30 * 60 * 1000L);
        return ResultVo.success(Messages.OK, null);
    }


}
