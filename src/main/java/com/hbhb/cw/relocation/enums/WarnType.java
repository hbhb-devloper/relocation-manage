package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum WarnType {

    /**
     * 已关闭
     */
    CLOSED(0),

    /**
     * 未处理
     */
    TO_PROCESS(1),

    /**
     * 处理中
     */
    PROCESSING(2),
    ;
    private final Integer value;

    WarnType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
