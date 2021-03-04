package com.hbhb.cw.relocation.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wxg
 * @since 2020-09-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectImportVO implements Serializable {
    private static final long serialVersionUID = -7934377717637388811L;

    @ExcelProperty(value = "区域", index = 0)
    private String unitName;

    @ExcelProperty(value = "迁改项目编号", index = 1)
    private String projectNum;

    @ExcelProperty(value = "EOMS迁移修缮管理流程工单号", index = 2)
    private String eomsRepairNum;

    @ExcelProperty(value = "EOMS光缆割接流程工单号", index = 3)
    private String eomsCutNum;

    @ExcelProperty(value = "计划施工时间", index = 4)
    private String planStartTime;

    @ExcelProperty(value = "计划完成时间", index = 5)
    private String planEndTime;

    @ExcelProperty(value = "实际完工时间", index = 6)
    private String actualEndTime;

    @ExcelProperty(value = "施工单位", index = 7)
    private String constructionUnit;

    @ExcelProperty(value = "工程名称", index = 8)
    private String projectName;

    @ExcelProperty(value = "迁改涉及网络层级（省干、汇聚、接入、驻地网）", index = 9)
    private String networkHierarchy;

    @ExcelProperty(value = "施工费(预算:元)", index = 10)
    private String constructionBudget;

    @ExcelProperty(value = "甲供材料费(预算:元)", index = 11)
    private String materialBudget;

    @ExcelProperty(value = "施工费(送审结算:元)", index = 12)
    private String constructionCost;

    @ExcelProperty(value = "甲供材料费(送审结算:元)", index = 13)
    private String materialCost;

    @ExcelProperty(value = "施工费审定金额(审计后:元)", index = 14)
    private String constructionAuditCost;

    @ExcelProperty(value = "主动迁改或者被动", index = 15)
    private String isInitiative;

    @ExcelProperty(value = "性质归类", index = 16)
    private String projectType;

    @ExcelProperty(value = "迁改原因", index = 17)
    private String cause;

    @ExcelProperty(value = "对方单位", index = 18)
    private String oppositeUnit;

    @ExcelProperty(value = "对方联系人", index = 19)
    private String oppositeContacts;

    @ExcelProperty(value = "对方联系电话", index = 20)
    private String oppositeContactsNum;

    @ExcelProperty(value = "有无赔补", index = 21)
    private String hasCompensation;

    @ExcelProperty(value = "被动无赔类型", index = 22)
    private String compensationType;

    @ExcelProperty(value = "合同编号", index = 23)
    private String contractNum;

    @ExcelProperty(value = "赔补合同名", index = 24)
    private String contractName;

    @ExcelProperty(value = "赔补金额（元）", index = 25)
    private String compensationAmount;

    @ExcelProperty(value = "赔补总额（元）", index = 26)
    private String totalCompensationAmount;

    @ExcelProperty(value = "预付款应付金额（元）", index = 27)
    private String anticipatePayable;

    @ExcelProperty(value = "预付款到账金额（元）", index = 28)
    private String anticipatePayment;

    @ExcelProperty(value = "决算款到账金额（元）\n" +
            "（注：决算款不包含预付款）", index = 29)
    private String finalPayment;

    @ExcelProperty(value = "赔补状态（合同签订中/预付款未开票/\n" +
            "预付款已开票未到账/\n" +
            "预付款已到账，施工中/\n" +
            "决算编制审计中/\n" +
            "决算款已开票未到账/\n" +
            "全额回款）注：必须从以上选项中", index = 30)
    private String compensationSate;

    @ExcelProperty(value = "未全额回款合同\n" +
            "合同签订时长（年）", index = 31)
    private String contractDuration;

    @ExcelProperty(value = "赔补特殊情况备注（赔补性质变更、决算款有调整或小于协议金额等特殊情况说明）", index = 32)
    private String compensationRemake;

    @ExcelProperty(value = "月报", index = 33)
    private String projectMonth;

    @ExcelProperty(value = "年份", index = 34)
    private String projectYear;

    @ExcelProperty(value = "合同类型", index = 35)
    private String contractType;
}
