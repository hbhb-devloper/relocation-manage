package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * @author hyk
 * @since 2020-09-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeExportVO implements Serializable {

    private static final long serialVersionUID = -1384092271081433149L;

    @ColumnWidth(10)
    @ExcelProperty(value = "序号", index = 0)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private Integer num;


    @ColumnWidth(10)
    @ExcelProperty(value = "类别", index = 1)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String category;


    @ColumnWidth(10)
    @ExcelProperty(value = "经办单位", index = 2)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String unit;


    @ColumnWidth(45)
    @ExcelProperty(value = "供应商", index = 3)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String supplier;


    @ColumnWidth(35)
    @ExcelProperty(value = "合同编号", index = 4)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String contractNum;


    @ColumnWidth(110)
    @ExcelProperty(value = "合同名称", index = 5)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String contractName;


    @ColumnWidth(20)
    @ExcelProperty(value = "起始时间", index = 6)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String startTime;


    @ColumnWidth(20)
    @ExcelProperty(value = "截止时间", index = 7)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String contractDeadline;


    @ColumnWidth(15)
    @ExcelProperty(value = "合同金额", index = 8)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String contractAmount;


    @ColumnWidth(20)
    @ExcelProperty(value = "开票时间", index = 9)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String invoiceTime;


    @ColumnWidth(15)
    @ExcelProperty(value = "发票号码", index = 10)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String invoiceNum;


    @ColumnWidth(20)
    @ExcelProperty(value = "发票类型", index = 11)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String invoiceType;


    @ColumnWidth(15)
    @ExcelProperty(value = "价款", index = 12)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String amount;


    @ColumnWidth(15)
    @ExcelProperty(value = "税额", index = 13)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String tax;


    @ColumnWidth(15)
    @ExcelProperty(value = "价税合计", index = 14)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String taxIncludeAmount;


    @ColumnWidth(30)
    @ExcelProperty(value = "工程名", index = 15)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String constructionName;


    @ColumnWidth(10)
    @ExcelProperty(value = "收款情况", index = 16)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String isReceived;


    @ColumnWidth(10)
    @ExcelProperty(value = "账龄（月）", index = 17)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private Integer aging;


    @ColumnWidth(15)
    @ExcelProperty(value = "应收", index = 18)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String receivable;


    @ColumnWidth(15)
    @ExcelProperty(value = "已收", index = 19)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String received;


    @ColumnWidth(15)
    @ExcelProperty(value = "未收", index = 20)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
        borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
        borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String unreceived;

}
