package com.zzw.chatserver.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";


    /**
     * 时间格式（yyyy-MM）
     */
    public static final String yyyy_MM = "yyyy-MM";


    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        if (!StringUtils.isEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                logger.error("Date parse error", e);
            }
        }
        return date;
    }

    // 获取两个时间相差（毫秒）
    public static long getTimeDelta(Date oldTime, Date newTime) {
        long NTime = newTime.getTime();
        long OTime = oldTime.getTime();
        return (NTime - OTime);
    }
}
