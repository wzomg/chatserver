package com.zzw.chatserver.utils;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class HttpServletRequestUtil {

    public static String getBodyTxt(ServletRequest request, String name) throws IOException {

        BufferedReader br = request.getReader();
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while ((str = br.readLine()) != null) {
            wholeStr.append(str);
        }
        log.info("解析得到的数据为：{}", wholeStr);
        return JSONObject.parseObject(wholeStr.toString()).getString(name);
    }
}
