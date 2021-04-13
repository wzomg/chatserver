package com.zzw.chatserver.utils;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_CLINET = "client";
    private static final String PREFIX_ONLINE_UID_SET = "online:uidSet";

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 在线用户集合的key
    public static String getOnlineUidSetKey() {
        return PREFIX_ONLINE_UID_SET;
    }

    //客户端信息key
    public static String getClientKey(String userId) {
        return PREFIX_CLINET + SPLIT + userId;
    }

}
