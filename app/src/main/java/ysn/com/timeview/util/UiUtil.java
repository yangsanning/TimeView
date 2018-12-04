package ysn.com.timeview.util;

import java.text.DecimalFormat;

/**
 * @Author yangsanning
 * @ClassName UiUtil
 * @Description 一句话概括作用
 * @Date 2018/12/4
 * @History 2018/12/4 author: description:
 */
public class UiUtil {
    private static final DecimalFormat DECIMAL_FORMAT_ZERO = new DecimalFormat("0");
    public static final DecimalFormat VOLUME_DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static String decimalFormat(float num) {
        return VOLUME_DECIMAL_FORMAT.format(num);
    }

    public static String getVolume(int volume) {
        if (volume < 10000) {
            return volume + "";
        } else if (volume < Math.pow(10000, 2)) {
            return volume / 10000 + "万";
        }
        if (volume < Math.pow(10000, 3)) {
            return (int) (volume / Math.pow(10000, 2)) + "亿";
        }
        return (int) (volume / Math.pow(10000, 3)) + "万亿";
    }

    public static String getDecimalZero(float value) {
        if (value < 10000) {
            return DECIMAL_FORMAT_ZERO.format(value);
        } else if (value < Math.pow(10000, 2)) {
            return DECIMAL_FORMAT_ZERO.format(value / 10000) + "万";
        } else if (value < Math.pow(10000, 3)) {
            return DECIMAL_FORMAT_ZERO.format(value / Math.pow(10000, 2)) + "亿";
        }
        return DECIMAL_FORMAT_ZERO.format(value / Math.pow(10000, 3)) + "万亿";
    }
}
