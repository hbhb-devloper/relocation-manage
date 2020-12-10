package com.hbhb.cw.relocation.enums;

import lombok.Getter;

/**
 * @author wangxiaogang
 */

@Getter
public enum RelocationErrorCode {
    RELOCATION_IMPORT_DATE_ERROR("80898", "relocation.import.date.error"),
    RELOCATION_IMPORT_DATE_REPETITION("80899", "relocation.import.date.repetition"),
    RELOCATION_INVOICE_IMPORT_ERROR("80900", "relocation.invoice.import.error"),
    RELOCATION_INCOME_IMPORT_ERROR("80901", "relocation.income.import.error"),
    RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR("80902", "relocation.income.import.bustype.error"),
    RELOCATION_INVOICE_REMAKE_ERROR("80902", "relocation.income.remake.error"),
    RELOCATION_FINANCE_QUERY_PARAM_ERROR("80903", "relocation.finance.query.param.error"),
    RELOCATION_INVOICE_EXIST_PROJECT_ERROR("80904", "relocation.invoice.exist.project.error"),
    RELOCATION_RECEIPT_IMPORT_ERROR("80906", "relocation.receipt.import.error"),
    FILE_DATA_NAME_ERROR("80907", "file.data.name.error"),
    RELOCATION_TEMPLATE_ERROR("80908", "relocation.template.error"),
    RELOCATION_CONTRACT_ERROR("80909", "relocation.contract.error"),
    RELOCATION_RECEIPT_CANT_MATCH("80910", "relocation.receipt.cant.match"),
    RELOCATION_RECEIPT_ALREADY_EXIST("80911", "relocation.receipt.already.exist"),
    RELOCATION_RECEIPT_REMAKE_ERROR("80011", "relocation.receipt.remake.error"),
    RELOCATION_PROJECT_PERMISSION_DENIED("80012", "relocation.project.permission.denied");


    private final String code;

    private final String message;

    RelocationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
