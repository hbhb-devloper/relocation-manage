package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum PaymentType {
    /**
     * 预付款
     */
    ADVANCE_PAYMENT(1, "预付款"),
    /**
     * 决算款
     */
    FINAL_PARAGRAPH(2, "决算款"),
    /**
     * 尾款
     */
    FINAL_PAYMENT(3, "尾款");
    private final Integer key;
    private final String value;

    PaymentType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer key() {
        return this.key;
    }

    public String value() {
        return this.value;
    }
}
