package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum IsReceived {

    /**
     * 已收款
     */
    RECEIVED(10, "已回款"),

    /**
     * 未收款
     */
    NOT_RECEIVED(20, "未收款"),
    /**
     * 部分回款
     */
    PART_RECEIVED(30, "部分回款");


    private final Integer key;
    private final String value;

    IsReceived(Integer key, String value) {
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
