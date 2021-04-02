package com.zzw.chatserver.utils;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_USER = "user";

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 用户信息key
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
