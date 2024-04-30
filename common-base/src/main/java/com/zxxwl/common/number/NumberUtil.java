package com.zxxwl.common.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字工具类
 *
 * @author qingyu
 */
public final class NumberUtil {
    /**
     * 字符串转double 保留 scale位 不进行四舍五入
     *
     * @param str   数字字符串
     * @param scale 保留几位
     * @return double
     */
    public static double geoStringToDouble(String str, int scale) {
        // 不进行四舍五入
        return new BigDecimal(str).setScale(scale, RoundingMode.DOWN).doubleValue();
    }
    /**
     * double 保留 scale位 不进行四舍五入
     *
     * @param v   数字字符串
     * @param scale 保留几位
     * @return double
     */
    public static double geoDoubleScale(double v, int scale) {
        // 不进行四舍五入
        return new BigDecimal(v).setScale(scale, RoundingMode.DOWN).doubleValue();
    }

    /**
     * 字符串转 double 不进行四舍五入
     *
     * @param str 数字字符串
     * @return double
     */
    public static double stringToDouble(String str) {
        // 不进行四舍五入
        return new BigDecimal(str).doubleValue();
    }

    /**
     * 字符串转 double 不进行四舍五入
     *
     * @param str 数字字符串
     * @return double
     */
    public static double geoStringToDouble(String str) {
        // 不进行四舍五入
        return geoStringToDouble(str, 8);
    }

    /**
     * double to string 不会丢失精度
     *
     * @param num num
     * @return doubleStr
     */
    public static String doubleToString(double num) {
        // 不进行四舍五入
        return new BigDecimal(num).toString();
    }

}
