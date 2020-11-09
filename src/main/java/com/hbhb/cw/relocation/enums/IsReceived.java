package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum IsReceived {

    /**
     * 收款编号
     */
    RECEIVED_CODE("1"),

    /**
     * 未收款编号
     */
    NOT_RECEIVED_CODE("0"),

    /**
     * 收款
     */
    RECEIVED("是"),

    /**
     * 未收款
     */
    NOT_RECEIVED("否"),
    ;


    private final String value;

    IsReceived(String value) {
        this.value = value;
    }


    public String value() {
        return this.value;
    }
}
