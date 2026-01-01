package com.zzw.chatserver.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {
    private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    private static final Long EXPIRE = 24 * 3600 * 1000L; //设置一天时间
    // Must be at least 256 bits (32 bytes)
    private static final String SECRET_STRING = "wzomg_chat_server_secret_key_must_be_long_enough_for_hs256"; 
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    //生成token
    public static String createJwt(String userId, String username) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(username, "用户名不能为空");
        return Jwts.builder()
                .subject(userId)
                .claim("userId", userId)
                .claim("username", username)
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();
    }

    //解析token
    public static Claims parseJwt(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //删除token
    public void removeToken(String token) {
    }
}