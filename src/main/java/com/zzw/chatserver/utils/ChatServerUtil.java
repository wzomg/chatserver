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

    //判断是否都为数字
    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    //验证邮箱
    public static boolean isEmail(String email) {
        /**
         *   ^匹配输入字符串的开始位置
         *   $结束的位置
         *   \转义字符 eg:\. 匹配一个. 字符  不是任意字符 ，转义之后让他失去原有的功能
         *   \t制表符
         *   \n换行符
         *   \\w匹配字符串  eg:\w不能匹配 因为转义了
         *   \w匹配包括字母数字下划线的任何单词字符
         *   \s包括空格制表符换行符
         *   *匹配前面的子表达式任意次
         *   .小数点可以匹配任意字符
         *   +表达式至少出现一次
         *   ?表达式0次或者1次
         *   {10}重复10次
         *   {1,3}至少1-3次
         *   {0,5}最多5次
         *   {0,}至少0次 不出现或者出现任意次都可以 可以用*号代替
         *   {1,}至少1次  一般用+来代替
         *   []自定义集合     eg:[abcd]  abcd集合里任意字符
         *   [^abc]取非 除abc以外的任意字符
         *   |  将两个匹配条件进行逻辑“或”（Or）运算
         *   [1-9] 1到9 省略123456789
         *    邮箱匹配 eg: ^[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\.){1,3}[a-zA-z\-]{1,}$
         *
         */
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        return Pattern.compile(RULE_EMAIL).matcher(email).matches();
    }
}
