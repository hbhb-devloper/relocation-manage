package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum State {

    /**
     * 状态0
     */
    ZERO("0"),
    /**
     * 状态1
     */
    ONE("1"),
    /**
     * 是
     */
    YES("是"),
    /**
     * 否
     */
    NO("否");
    private final String value;

    State(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
