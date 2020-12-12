package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum Category {
    /**
     * 迁改
     */
    RELOCATION(1, "迁改"),

    /**
     * 搬迁
     */
    REMOVAL(2, "搬迁"),

    /**
     * 代建
     */
    CONSTRUCTION(3, "代建");

    private final Integer key;

    private final String value;

    Category(Integer key, String value) {
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
