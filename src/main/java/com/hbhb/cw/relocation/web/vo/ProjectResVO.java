package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiaokang
 * @since 2020-09-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResVO implements Serializable {
    private static final long serialVersionUID = -5036826689710501502L;

    private Long id;

    @Schema(description = "区域(单位id)")
    private Integer unitId;

    @Schema(description = "区域(单位id)")
    private String unitName;

    @Schema(description = "迁改项目编号")
    private String projectNum;

    @Schema(description = "EOMS迁移修缮管理流程工单号")
    private String eomsRepairNum;

    @Schema(description = "EOMS光缆割接流程工单号")
    private String eomsCutNum;

    @Schema(description = "计划实施时间")
    private String planStartTime;

    @Schema(description = "计划完成时间")
    private String planEndTime;

    @Schema(description = "实际结束时间")
    private String actualEndTime;

    @Schema(description = "施工单位")
    private String constructionUnit;

    @Schema(description = "工程名称")
    private String projectName;

    @Schema(description = "迁改涉及网络层级")
    private String networkHierarchy;

    @Schema(description = "施工费（预算：元）")
    private String constructionBudget;

    @Schema(description = "甲供材料费(预算:元)")
    private String materialBudget;

    @Schema(description = "施工费(送审结算:元)")
    private String constructionCost;

    @Schema(description = "甲供材料费(送审结算:元)")
    private String materialCost;

    @Schema(description = "施工费审定金额(审计后:元)")
    private String constructionAuditCost;

    @Schema(description = "主动迁改或者被动")
    private Boolean isInitiative;

    @Schema(description = "项目类型(性质归类)")
    private String projectType;

    @Schema(description = "迁改原因")
    private String cause;

    @Schema(description = "对方单位")
    private String oppositeUnit;

    @Schema(description = "对方联系人")
    private String oppositeContacts;

    @Schema(description = "对方联系电话")
    private String oppositeContactsNum;

    @Schema(description = "有无赔补(0-没有、1-有)")
    private Boolean hasCompensation;

    @Schema(description = "被动补偿类型")
    private String compensationType;

    @Schema(description = "合同编号")
    private String contractNum;

    @Schema(description = "补偿合同名称")
    private String contractName;

    @Schema(description = "补偿金额")
    private String compensationAmount;

    @Schema(description = "赔补总额")
    private String totalCompensationAmount;

    @Schema(description = "预付款应付金额（元）")
    private String anticipatePayable;

    @Schema(description = "预付款到账金额（元）")
    private String anticipatePayment;

    @Schema(description = "决算款到账金额（元）")
    private String finalPayment;

    @Schema(description = "补偿状态")
    private String compensationSate;

    @Schema(description = "未全额回款合同历时")
    private String contractDuration;

    @Schema(description = "赔补特殊情况备注")
    private String compensationRemake;

    @Schema(description = "月报")
    private String projectMonth;

    @Schema(description = "年份")
    private String projectYear;

    @Schema(description = "合同类型")
    private String contractType;

    @Schema(description = "文件id")
    private String fileId;

}
