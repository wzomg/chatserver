package com.zzw.chatserver.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.UUID;

public class JwtUtils {
    private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    private static final Long EXPIRE = 24 * 3600 * 1000L; //设置一天时间
    private static final String SECRET = "wzomg"; //用于signature（签名）部分解密

    //生成token
    public static String createJwt(String userId, String username) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(username, "用户名不能为空");
        return Jwts.builder().setSubject(userId)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    //解析token
    public static Claims parseJwt(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    //删除token
    public void removeToken(String token) {
    }
}