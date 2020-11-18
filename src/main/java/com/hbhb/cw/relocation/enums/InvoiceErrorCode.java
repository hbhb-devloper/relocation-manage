package com.hbhb.cw.relocation.enums;

import lombok.Getter;

/**
 * @author xiaokang
 * @since 2020-10-06
 */
@Getter
public enum InvoiceErrorCode {

    RELOCATION_INVOICE_IMPORT_ERROR("200", "relocation.invoice.import.error"),
    RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR("201", "relocation.income.import.bustype.error"),
    RELOCATION_INVOICE_REMAKE_ERROR("202", "relocation.invoice.remake.error"),
    RELOCATION_INVOICE_EXIST_PROJECT_ERROR("203", "relocation.invoice.exist.project.error"),
    RELOCATION_INCOME_IMPORT_ERROR("204", "relocation.income.import.error"),
    RELOCATION_FINANCE_QUERY_PARAM_ERROR("205", "relocation.finance.query.param.error"),
    FILE_NAME_ERROR("220", "file.data.name.error"),
    ;

    private String code;

    private String message;

    InvoiceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
