package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum UnitAbbr {
    /**
     * 网络部
     */
    WLB("网络部"),

    /**
     * 财务部
     */
    CWB("财务部"),

    ;

    private final String value;

    UnitAbbr(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

