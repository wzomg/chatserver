package com.zzw.chatserver.utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class SystemUtil {

    private static OperatingSystemMXBean operatingSystemMXBean;

    static {
        operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * 获取cpu使用率
     */
    public static double getSystemCpuLoad() {
        return operatingSystemMXBean.getSystemCpuLoad();
    }

    /**
     * 获取cpu数量
     */
    public static int getSystemCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取内存使用率
     */
    public static double getSystemMemLoad() {
        double totalMem = operatingSystemMXBean.getTotalPhysicalMemorySize();
        double freeMem = operatingSystemMXBean.getFreePhysicalMemorySize();
        return (totalMem - freeMem) / totalMem;
    }
}