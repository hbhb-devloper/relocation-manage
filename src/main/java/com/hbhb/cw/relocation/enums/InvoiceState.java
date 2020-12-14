package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum InvoiceState {

    /**
     * 红字
     */
    RED_STATE(1, "红字"),

    /**
     * 蓝字
     */
    BLUE_STATE(2, "蓝字");

    private final String value;

    private final Integer key;

    InvoiceState(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer key() {
        return key;
    }

    public String value() {
        return this.value;
    }
}
