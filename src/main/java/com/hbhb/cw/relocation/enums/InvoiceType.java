package com.hbhb.cw.relocation.enums;

/**
 * @author wangxiaogang
 */

public enum InvoiceType {

    /**
     * 增值税普通发票
     */
    PLAIN_INVOICE(10, "增值税普通发票"),

    /**
     * 增值税专票
     */
    SPECIAL_INVOICE(20, "增值税专用发票"),

    /**
     * 增值税电子普通发票
     */
    ELECTRONIC_PLAIN_INVOICE(30, "增值税电子普通发票"),

    /**
     * 电子增值税专用发票
     */
    ELECTRONIC_SPECIAL_INVOICE(40, "电子增值税专用发票"),

    /**
     * 收据
     */
    RECEIPT(50, "收据"),
    /**
     * 增值税普票
     */
    PLAIN_INVOICE_ORDINARY(60, "增值税普票");


    private final Integer key;
    private final String value;

    InvoiceType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }


    public String value() {
        return this.value;
    }

    public Integer key() {
        return this.key;
    }
}
