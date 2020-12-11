package com.hbhb.cw.relocation.util;

import java.math.BigDecimal;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
public class BigDecimalUtil {

    /**
     * 字符串转换为BigDecimal金额类型
     */
    public static BigDecimal getBigDecimal(String str) {
        if (!isEmpty(str)) {
            str = str.replaceAll("[\\s\\u00A0]+", "").trim();
            str = str.trim();

        }
        if (isEmpty(str)) {
            str = "0";
        } else if (str.contains("，")) {
            str = str.replace("，", "");
        } else if (str.contains(",")) {
            str = str.replace(",", "");
        }
        return new BigDecimal(str);
    }
}
