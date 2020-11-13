package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hyk
 * @since 2020-10-9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class FinanceResVO implements Serializable {

    private static final long serialVersionUID = -7894679294000895303L;

    @ExcelIgnore
    private Long id;

    @ExcelIgnore
    private Integer unitId;

    @ColumnWidth(10)
    @ExcelProperty(value = "县市", index = 0)
    private String unit;

    @ColumnWidth(10)
    @ExcelProperty(value = "项目类型", index = 1)
    private String projectType;

    @ColumnWidth(30)
    @ExcelProperty(value = "EOMS工单号", index = 2)
    private String eomsRepairNum;

    @ColumnWidth(60)
    @ExcelProperty(value = "项目名称", index = 3)
    private String projectName;

    @ColumnWidth(20)
    @ExcelProperty(value = "项目割接或完成时间", index = 4)
    private String planEndTime;

    @ColumnWidth(15)
    @ExcelProperty(value = "预算费用", index = 5)
    private String estimatedCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "材料费", index = 6)
    private String materialCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "施工费", index = 7)
    private String constructionCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "项目约定赔补金额", index = 8)
    private String compensationAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "理赔支付方式", index = 9)
    private String payType;

    @ColumnWidth(15)
    @ExcelProperty(value = "立项时间", index = 10)
    private String planStartTime;

    @ColumnWidth(15)
    @ExcelProperty(value = "完工时间", index = 11)
    private String actualEndTime;

    @ColumnWidth(35)
    @ExcelProperty(value = "合同编号", index = 12)
    private String contractNum;

    @ColumnWidth(80)
    @ExcelProperty(value = "合同名称", index = 13)
    private String contractName;

    @ColumnWidth(30)
    @ExcelProperty(value = "合同甲方", index = 14)
    private String oppositeUnit;

    @ColumnWidth(10)
    @ExcelProperty(value = "合同经办人", index = 15)
    private String oppositeContacts;

    @ColumnWidth(15)
    @ExcelProperty(value = "合同金额", index = 16)
    private String oppositeAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "初始化已回收金额", index = 17)
    @Schema(description = "初始化回收金额【2019年底】")
    private String initRecoveredAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "当年已回收金额", index = 18)
    @Schema(description = "当年已回收金额")
    private String yearRecoveredAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "预付款应收金额", index = 19)
    private String advanceReceivableAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "预付款已收金额", index = 20)
    private String advanceReceivedAmount;

    @ColumnWidth(10)
    @ExcelProperty(value = "预付款是否完全到账", index = 21)
    @Schema(description = "预付款是否完全到账1:是,0:否")
    private String isAllReceived;

    @ColumnWidth(10)
    @ExcelProperty(value = "当前年份", index = 22)
    @Schema(description = "当前年份")
    private String currentYear;

    @ColumnWidth(10)
    @ExcelProperty(value = "一月回款", index = 23)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @Schema(description = "1月收款")
    private String janReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "二月回款", index = 24)
    @Schema(description = "2月收款")
    private String febReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "三月回款", index = 25)
    @Schema(description = "3月收款")
    private String marReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "四月回款", index = 26)
    @Schema(description = "4月收款")
    private String aprReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "五月回款", index = 27)
    @Schema(description = "5月收款")
    private String mayReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "六月回款", index = 28)
    @Schema(description = "6月收款")
    private String juneReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "七月回款", index = 29)
    @Schema(description = "7月收款")
    private String julReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "八月回款", index = 30)
    @Schema(description = "8月收款")
    private String augReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "九月回款", index = 31)
    @Schema(description = "9月收款")
    private String sepReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十月回款", index = 32)
    @Schema(description = "10月收款")
    private String octReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十一月回款", index = 33)
    @Schema(description = "11月收款")
    private String novReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十二月回款", index = 34)
    @Schema(description = "12月收款")
    private String decReceivable;

    @ColumnWidth(15)
    @ExcelProperty(value = "待回收金额", index = 35)
    @Schema(description = "待回收金额")
    private String unpaidCollection;

    @ColumnWidth(15)
    @ExcelProperty(value = "已开票金额", index = 36)
    @Schema(description = "已开票金额")
    private String invoicedAmount;

}
