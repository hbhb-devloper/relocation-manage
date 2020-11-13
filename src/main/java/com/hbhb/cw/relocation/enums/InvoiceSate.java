package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum InvoiceSate {

    /**
     * 红字
     */
    RED_STATE("红字"),

    /**
     * 蓝字
     */
    BLUE_STATE("篮字");

    private final String value;

    InvoiceSate(String value) {
        this.value = value;
    }


    public String value() {
        return this.value;
    }
}
