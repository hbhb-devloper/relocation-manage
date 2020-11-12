package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum AmountType {

    /**
     * 决算款
     */
    final_payment("决算款"),

    /**
     * 预付款
     */
    advance_payment("预付款"),
    /**
     * 尾款
     */
    balance_payment("尾款"),
    ;

    private final String value;

    AmountType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
