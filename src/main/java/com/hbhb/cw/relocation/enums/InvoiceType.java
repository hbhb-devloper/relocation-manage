package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum InvoiceType {

    /**
     * 增值税普票
     */
    PLAIN_INVOICE("增值税普通发票"),

    /**
     * 增值税专票
     */
    SPECIAL_INVOICE("增值税专用发票"),

    /**
     * 增值税电子普通发票
     */
    ELECTRONIC_PLAIN_INVOICE("增值税电子普通发票"),

    /**
     * 电子增值税专用发票
     */
    ELECTRONIC_SPECIAL_INVOICE("电子增值税专用发票");
    ;

    private final String value;

    InvoiceType(String value) {
        this.value = value;
    }


    public String value() {
        return this.value;
    }
}
