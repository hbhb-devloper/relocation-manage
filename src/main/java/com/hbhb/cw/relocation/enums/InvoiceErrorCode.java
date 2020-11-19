package com.hbhb.cw.relocation.enums;

import lombok.Getter;

/**
 * @author xiaokang
 * @since 2020-10-06
 */
@Getter
public enum InvoiceErrorCode {

    RELOCATION_INVOICE_IMPORT_ERROR("80000", "relocation.invoice.import.error"),
    RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR("80001", "relocation.income.import.bustype.error"),
    RELOCATION_INVOICE_REMAKE_ERROR("80002", "relocation.invoice.remake.error"),
    RELOCATION_INVOICE_EXIST_PROJECT_ERROR("80003", "relocation.invoice.exist.project.error"),
    RELOCATION_INCOME_IMPORT_ERROR("80004", "relocation.income.import.error"),
    RELOCATION_FINANCE_QUERY_PARAM_ERROR("80005", "relocation.finance.query.param.error"),
    FILE_NAME_ERROR("80006", "file.data.name.error"),
    RELOCATION_INCOME_NOT_PROJECT("80007", "relocation.income.not.project"),
    ;

    private String code;

    private String message;

    InvoiceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
