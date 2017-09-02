package com.xahaolan.emanage.utils.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by marine on 16/3/14.
 */
public class NumberUtil {

    public static String formatFloat(float f, int newScale) {
        return new BigDecimal(f).setScale(newScale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     *                       四舍五入 保留num位小数
     *
     * @param value  处理值
     * @param num    几位小数
     */
    public static Double numberKeep1(Double value, int num) {
        BigDecimal bg = new BigDecimal(value);
        double f1 = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     *                      四舍五入 保留num位小数 数字超过三位带逗号
     * @param value
     * @param num
     * @return
     */
    public static String numberKeep2(Double value, int num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);
        return nf.format(value);
    }

    /**
     *                     四舍五入 显示两位小数
     *
     * DecimalFormat转换最简便
     */
    public static String numberKeep3(Object num) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (num != null){
           return df.format(num);
        }
        return "";
    }
    /**
     *                     四舍五入 保留整数
     *
     * DecimalFormat转换最简便
     */
    public static String numberKeep4(Object num) {
        DecimalFormat df = new DecimalFormat("#0");
        if (num != null){
            return df.format(num);
        }
        return "";
    }

    /**
     * String.format打印最简便
     */
    public void numberKeep5(Double num) {
        System.out.println(String.format("%.2f", num));
    }
}
