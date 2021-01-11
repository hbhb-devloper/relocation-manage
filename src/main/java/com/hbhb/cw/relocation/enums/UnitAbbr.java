package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum UnitAbbr {
    /**
     * 网络部
     */
    WLB("网络部", 287),

    /**
     * 财务部
     */
    CWB("财务部", 283),

    ;

    private final Integer key;
    private final String value;


    public Integer key() {
        return this.key;
    }

    UnitAbbr(String value, Integer key) {
        this.value = value;
        this.key = key;
    }

    public String value() {
        return this.value;
    }
}

