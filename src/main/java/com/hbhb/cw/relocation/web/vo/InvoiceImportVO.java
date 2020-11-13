package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyk
 * @since 2020-09-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceImportVO implements Serializable {

    private static final long serialVersionUID = -7934377717637388811L;

    @ExcelProperty(value = "序号", index = 0)
    private String number;

    @ExcelProperty(value = "地市", index = 1)
    private String district;

    @ExcelProperty(value = "县市", index = 2)
    private String unitId;

    @ExcelProperty(value = "发票代码", index = 3)
    private String invoiceCode;

    @ExcelProperty(value = "发票号码", index = 4)
    private String invoiceNumber;

    @ExcelProperty(value = "开票点", index = 5)
    private String invoiceSite;

    @ExcelProperty(value = "业务类型", index = 6)
    private String businessType;

    @ExcelProperty(value = "发票类型", index = 7)
    private String invoiceType;

    @ExcelProperty(value = "购方税号", index = 8)
    private String buyerTax;

    @ExcelProperty(value = "购方名称", index = 9)
    private String buyerName;

    @ExcelProperty(value = "开票项目", index = 10)
    private String invoiceProject;

    @ExcelProperty(value = "开票日期", index = 11)
    private String invoiceTime;

    @ExcelProperty(value = "金额", index = 12)
    private String amount;

    @ExcelProperty(value = "税率", index = 13)
    private String taxRate;

    @ExcelProperty(value = "税额", index = 14)
    private String taxAmount;

    @ExcelProperty(value = "价税合计", index = 15)
    private String taxIncludeAmount;

    @ExcelProperty(value = "备注", index = 16)
    private String remake;

    @ExcelProperty(value = "申请人", index = 17)
    private String applicant;

    @ExcelProperty(value = "开票人", index = 18)
    private String issuer;

    @ExcelProperty(value = "票据状态", index = 19)
    private String state;

    @ExcelProperty(value = "是否为自定义菜单开票", index = 20)
    private String isImport;

    @ExcelProperty(value = "客户经理", index = 21)
    private String manager;

    @ExcelProperty(value = "备注修改列：（统一格式）合同号；区县；款项性质；项目信息", index = 22)
    private String newRemake;
}
