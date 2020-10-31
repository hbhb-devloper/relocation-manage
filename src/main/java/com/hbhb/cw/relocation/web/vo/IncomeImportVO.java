package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyk
 * @since 2020-09-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeImportVO implements Serializable {

    private static final long serialVersionUID = -1384092271081433149L;

    @ExcelProperty(value = "序号", index = 0)
    private String num;

    @ExcelProperty(value = "类别", index = 1)
    private String category;

    @ExcelProperty(value = "经办单位", index = 2)
    private String unit;

    @ExcelProperty(value = "供应商", index = 3)
    private String supplier;

    @ExcelProperty(value = "合同编号", index = 4)
    private String contractNum;

    @ExcelProperty(value = "合同名称", index = 5)
    private String contractName;

    @ExcelProperty(value = "起始时间", index = 6)
    private String startTime;

    @ExcelProperty(value = "合同截止时间", index = 7)
    private String contractDeadline;

    @ExcelProperty(value = "合同金额", index = 8)
    private String contractAmount;

    @ExcelProperty(value = "开票日期", index = 9)
    private String invoiceTime;

    @ExcelProperty(value = "发票号码", index = 10)
    private String invoiceNum;

    @ExcelProperty(value = "发票类型", index = 11)
    private String invoiceType;

    @ExcelProperty(value = "价款", index = 12)
    private String amount;

    @ExcelProperty(value = "税额", index = 13)
    private String tax;

    @ExcelProperty(value = "价税合计", index = 14)
    private String taxIncludeAmount;

    @ExcelProperty(value = "工程名", index = 15)
    private String constructionName;

    @ExcelProperty(value = "收款情况", index = 16)
    private String isReceived;

    @ExcelProperty(value = "账龄分类", index = 17)
    private String amountType;

    @ExcelProperty(value = "账龄（月）", index = 18)
    private Integer aging;

    @ExcelProperty(value = "应收", index = 19)
    private String receivable;

    @ExcelProperty(value = "已收", index = 20)
    private String received;

    @ExcelProperty(value = "未收", index = 21)
    private String unreceived;

    @ExcelProperty(value = "收款类型", index = 22)
    private String paymentType;

    @ExcelProperty(value = "当月收款金额", index = 23)
    private String monthAmount;

    @ExcelProperty(value = "收款单号", index = 24)
    private String receiptNum;

    @ExcelProperty(value = "收款人", index = 25)
    private String payee;
}
