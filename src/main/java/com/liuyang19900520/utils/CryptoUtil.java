package com.liuyang19900520.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * Created by liuyang on 2018/3/17
 */
@Component
public class CryptoUtil {

    private static String APP_KEY;
    private static final String SECRET_KEY_JWT = "*(-=4eklfasdfarerf41585fdasf";

    @Value("${appkey}")
    public void setDatabase(String appkey) {
        APP_KEY = appkey;
    }

    // HMAC 加密算法名称
    public static final String HMAC_MD5 = "HmacMD5";// 128位
    public static final String HMAC_SHA1 = "HmacSHA1";// 126
    public static final String HMAC_SHA256 = "HmacSHA256";// 256
    public static final String HMAC_SHA512 = "HmacSHA512";// 512

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static CompressionCodecResolver CODECRESOLVER = new DefaultCompressionCodecResolver();

    /**
     * JWT签发令牌
     *
     * @param subject     用户ID
     * @param roles       访问主张-角色
     * @param permissions 访问主张-权限
     * @return json web token
     */
    public static String issueJwt(String id, String subject, String roles
            , String permissions, Date issuedAt) {

        // 秘钥
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY_JWT);
        JwtBuilder jwt = Jwts.builder();
        jwt.setId(id);
        // 用户名
        jwt.setSubject(subject);
        // 签发者
        jwt.setIssuer("token-server");
        // 签发时间
        jwt.setIssuedAt(issuedAt);
        // 有效时间
        Date expiration = new Date(issuedAt.getTime() + 6000000000000000000L);
        jwt.setExpiration(expiration);

        // 访问主张-角色
        if (null != roles && !"".equals(roles)) {
            jwt.claim("roles", roles);
        }
        // 访问主张-权限
        if (null != permissions && !"".equals(permissions)) {
            jwt.claim("perms", permissions);
        }
        jwt.compressWith(CompressionCodecs.DEFLATE);
        jwt.signWith(SignatureAlgorithm.HS512, secretKeyBytes);
        return jwt.compact();
    }

    public static String verifyToken(String token) {
        Jwt parse = Jwts.parser()
                .setSigningKey(SECRET_KEY_JWT).parse(token);

        Claims claims = (Claims) parse.getBody();
        String verifyToken = issueJwt(claims.getId(), claims.getSubject(), String.valueOf(claims.get("roles")), String.valueOf(claims.get("perms")), claims.getIssuedAt());

        return verifyToken;
    }

    /**
     * 生成HMAC摘要
     *
     * @param plaintext 明文
     * @return 摘要
     */
    public static String hmacDigest(String plaintext) {
        try {
            Mac mac = Mac.getInstance(HMAC_MD5);
            byte[] secretByte = APP_KEY.getBytes();
            byte[] dataBytes = plaintext.getBytes();
            SecretKey secret = new SecretKeySpec(secretByte, HMAC_MD5);
            mac.init(secret);
            byte[] doFinal = mac.doFinal(dataBytes);
            return byte2HexStr(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    private static String byte2HexStr(byte[] bytes) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; bytes != null && n < bytes.length; n++) {
            stmp = Integer.toHexString(bytes[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }


}
