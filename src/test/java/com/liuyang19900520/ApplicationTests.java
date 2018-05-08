package com.liuyang19900520;

import com.google.common.base.Splitter;
import com.liuyang19900520.service.EmailService;
import com.liuyang19900520.utils.CryptoUtil;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Random;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    private static final String SECRET_KEY = "*(-=4eklfasdfarerf41585fdasf";

    private static String APP_KEY = "E2B7B173A8801CD1455878A1383B2337";

    @Autowired
    EmailService emailService;

    @Test
    public void applyToken() {

        Long current = System.currentTimeMillis();
        String url = "http://localhost:8080/tokenServer/auth/apply-token";
        MultiValueMap<String, Object> dataMap = new LinkedMultiValueMap<String, Object>();
        String clientKey = "super";// 客户端标识（用户名）
        String timeStamp = current.toString();// 时间戳

        String baseString = clientKey + timeStamp;
        String digest = CryptoUtil.hmacDigest(baseString);// 生成HMAC摘要

        System.out.println(digest + "===================================================================================");
        System.out.println(timeStamp + "===================================================================================");
        System.out.println(clientKey + "===================================================================================");

//        System.out.println(parse.toString());

        String stores = "0";
        Iterable<String> splitStores = Splitter.on(',').split(stores);

        for (String storeStr : splitStores) {
            if (StringUtils.isBlank(storeStr)) {
                break;
            }
        }
        emailService.sendSimpleMessage("576996936@qq.com", "test", "liuyang test");


    }


}
