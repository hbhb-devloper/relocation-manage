package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum WarnType {
    /**
     * 状态0
     */
    START_WARN(0),
    /**
     * 状态1
     */
    FINAL_WARN(1),
    ;
    private final Integer value;

    WarnType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
