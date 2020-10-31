package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

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

    @ColumnWidth(10)
    @ExcelProperty(value = "县市", index = 0)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "县市")
    private String unit;

    @ColumnWidth(10)
    @ExcelProperty(value = "项目类型", index = 1)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "项目类型")
    private String projectType;

    @ColumnWidth(30)
    @ExcelProperty(value = "EOMS工单号", index = 2)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "EOMS工单号")
    private String eomsRepairNum;

    @ColumnWidth(60)
    @ExcelProperty(value = "项目名称", index = 3)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "项目名称")
    private String projectName;

    @ColumnWidth(20)
    @ExcelProperty(value = "项目割接或完成时间", index = 4)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "项目割接或完成时间")
    private String planEndTime;

    @ColumnWidth(15)
    @ExcelProperty(value = "预算费用", index = 5)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "预算费用")
    private String estimatedCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "材料费", index = 6)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "材料费")
    private String materialCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "施工费", index = 7)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "施工费")
    private String constructionCost;

    @ColumnWidth(15)
    @ExcelProperty(value = "项目约定赔补金额", index = 8)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "赔补金额")
    private String compensationAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "理赔支付方式", index = 9)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "理赔金额支付方式")
    private String payType;

    @ColumnWidth(15)
    @ExcelProperty(value = "立项时间", index = 10)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "立项时间")
    private String planStartTime;

    @ColumnWidth(15)
    @ExcelProperty(value = "完工时间", index = 11)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "实际完工时间")
    private String actualEndTime;

    @ColumnWidth(35)
    @ExcelProperty(value = "合同编号", index = 12)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "合同编号")
    private String contractNum;

    @ColumnWidth(80)
    @ExcelProperty(value = "合同名称", index = 13)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "合同名称")
    private String contractName;

    @ColumnWidth(30)
    @ExcelProperty(value = "合同甲方", index = 14)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "合同甲方")
    private String oppositeUnit;

    @ColumnWidth(10)
    @ExcelProperty(value = "合同经办人", index = 15)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "合同经办人")
    private String oppositeContacts;

    @ColumnWidth(15)
    @ExcelProperty(value = "合同金额", index = 16)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "合同金额")
    private String oppositeAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "初始化已回收金额", index = 17)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "初始化回收金额【2019年底】")
    private String initRecoveredAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "当年已回收金额", index = 18)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "当年已回收金额")
    private String yearRecoveredAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "预付款应收金额", index = 19)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "预付款应收金额")
    private String advanceReceivableAmount;

    @ColumnWidth(15)
    @ExcelProperty(value = "预付款已收金额", index = 20)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "预付款已收金额")
    private String advanceReceivedAmount;

    @ColumnWidth(10)
    @ExcelProperty(value = "预付款是否完全到账", index = 21)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "预付款是否完全到账1:是,0:否")
    private String isAllReceived;

    @ColumnWidth(10)
    @ExcelProperty(value = "当前年份", index = 22)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "当前年份")
    private String currentYear;

    @ColumnWidth(10)
    @ExcelProperty(value = "一月回款", index = 23)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "1月收款")
    private String janReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "二月回款", index = 24)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "2月收款")
    private String febReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "三月回款", index = 25)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "3月收款")
    private String marReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "四月回款", index = 26)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "4月收款")
    private String aprReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "五月回款", index = 27)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "5月收款")
    private String mayReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "六月回款", index = 28)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "6月收款")
    private String juneReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "七月回款", index = 29)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "7月收款")
    private String julReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "八月回款", index = 30)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "8月收款")
    private String augReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "九月回款", index = 31)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "9月收款")
    private String sepReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十月回款", index = 32)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "10月收款")
    private String octReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十一月回款", index = 33)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "11月收款")
    private String novReceivable;

    @ColumnWidth(10)
    @ExcelProperty(value = "十二月回款", index = 34)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "12月收款")
    private String decReceivable;

    @ColumnWidth(15)
    @ExcelProperty(value = "待回收金额", index = 35)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "待回收金额")
    private String unpaidCollection;

    @ColumnWidth(15)
    @ExcelProperty(value = "已开票金额", index = 36)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "已开票金额")
    private String invoicedAmount;

}
