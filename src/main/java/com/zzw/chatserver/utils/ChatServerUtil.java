package com.zzw.chatserver.utils;

import com.zzw.chatserver.common.ConstValueEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatServerUtil {
    // MD5加密
    // hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //生成验证码
    public static String generatorCode() {
        StringBuilder code = new StringBuilder();
        int random;
        for (int i = 0; i < 4; i++) {
            random = (int) Math.floor(Math.random() * ConstValueEnum.cvCodeList.length);
            code.append(ConstValueEnum.cvCodeList[random]);
        }
        return code.toString();
    }

    // 生成随机nickname
    public static String randomNickname() {
        int len = ConstValueEnum.nickNameList.length;
        int random = (int) Math.floor(Math.random() * len);
        String res = str2HexStr(new Date().toString());
        return ConstValueEnum.nickNameList[random] + res.substring(res.length() - 2) + random;
    }

    //字符串转换成为16进制(无需Unicode编码)
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");

    //判断是否都为数字
    public static boolean isNumeric(String str) {
        return NUMERIC_PATTERN.matcher(str).matches();
    }

    //验证邮箱
    public static boolean isEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
