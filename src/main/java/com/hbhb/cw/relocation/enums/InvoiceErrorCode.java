package com.hbhb.cw.relocation.enums;

import lombok.Getter;

/**
 * @author xiaokang
 * @since 2020-10-06
 */
@Getter
public enum InvoiceErrorCode {

    RELOCATION_INVOICE_IMPORT_ERROR("200","迁改发票导入失败!"),
    RELOCATION_INVOICE_IMPORT_BUSTYPE_ERROR("201","发票业务类型只可为财务开票!"),
    RELOCATION_INVOICE_REMAKE_ERROR("202","备注列格式内容有误!"),
    RELOCATION_INVOICE_EXIST_PROJECT_ERROR("203","该发票查询不到对应项目!"),
    RELOCATION_INCOME_IMPORT_ERROR("204","迁改收款导入失败!"),
    RELOCATION_FINANCE_QUERY_PARAM_ERROR("205","查询参数输入有误!"),



    FILE_NAME_ERROR("220", "文件名类型错误"),



    ;

    private String code;

    private String message;

    InvoiceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
