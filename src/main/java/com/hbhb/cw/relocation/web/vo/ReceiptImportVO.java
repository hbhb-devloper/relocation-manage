package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wxg
 * @since 2020-09-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptImportVO {


    @ExcelProperty(value = "类别", index = 0)
    private String category;

    @ExcelProperty(value = "区域", index = 1)
    private String unit;

    @ExcelProperty(value = "收据编号", index = 2)
    private String receiptNum;

    @ExcelProperty(value = "赔补金额", index = 3)
    private BigDecimal compensationAmount;

    @ExcelProperty(value = "已到账金额", index = 4)
    private BigDecimal paymentAmount;

    @ExcelProperty(value = "赔补合同名", index = 5)
    private String contractName;

    @ExcelProperty(value = "合同编号", index = 6)
    private String contractNum;

    @ExcelProperty(value = "赔补金额到账情况说明", index = 7)
    private String paymentDesc;

    @ExcelProperty(value = "2020年开收据（元）", index = 8)
    private BigDecimal receiptAmount;

    @ExcelProperty(value = "开收据时间", index = 9)
    private String receiptTime;

    @ExcelProperty(value = "备注修改列：（统一格式）合同号；区县；款项性质；项目信息", index = 10)
    private String remake;

    @ExcelIgnore
    private Long projectId;
}
