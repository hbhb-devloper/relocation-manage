package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    @ExcelProperty(value = "类别", index = 1)
    private String category;

    @ExcelProperty(value = "经办单位", index = 2)
    private String unit;

    @ExcelProperty(value = "迁改收款责任人", index = 3)
    private String payee;

    @ExcelProperty(value = "供应商", index = 4)
    private String supplier;

    @ExcelProperty(value = "合同编号", index = 5)
    private String contractNum;

    @ExcelProperty(value = "合同名称", index = 6)
    private String contractName;

    @ExcelProperty(value = "起始时间", index = 7)
    private String startTime;

    @ExcelProperty(value = "合截止时间", index = 8)
    private String contractDeadline;

    @ExcelProperty(value = "合同金额", index = 9)
    private String contractAmount;

    @ExcelProperty(value = "开票日期", index = 10)
    private String invoiceTime;

    @ExcelProperty(value = "发票号码", index = 11)
    private String invoiceNumber;

    @ExcelProperty(value = "发票类型", index = 12)
    private String invoiceType;

    @ExcelProperty(value = "价款", index = 13)
    private String amount;

    @ExcelProperty(value = "税额", index = 14)
    private String taxAmount;

    @ExcelProperty(value = "价税合计", index = 15)
    private String taxIncludeAmount;

    @ExcelProperty(value = "工程名", index = 16)
    private String constructionName;

    @ExcelProperty(value = "收款情况", index = 17)
    private String isReceived;

    @ExcelProperty(value = "账龄分类", index = 18)
    private String amountType;

    @ExcelProperty(value = "账龄（月）", index = 19)
    private Integer aging;

    @ExcelProperty(value = "计提编号", index = 20)
    private String accrualNumber;

    @ExcelProperty(value = "收款编号", index = 21)
    private String receiptNum;

    @ExcelProperty(value = "账期", index = 22)
    private Integer paymentDay;

    @ExcelProperty(value = "备注", index = 23)
    private String remake;

}
