package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaokang
 * @since 2020-09-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResVO implements Serializable {
    private static final long serialVersionUID = -5036826689710501502L;

    private Long id;

    @ApiModelProperty("区域(单位id)")
    private Integer unitId;

    @ApiModelProperty("迁改项目编号")
    private String projectNum;

    @ApiModelProperty("EOMS迁移修缮管理流程工单号")
    private String eomsRepairNum;

    @ApiModelProperty("EOMS光缆割接流程工单号")
    private String eomsCutNum;

    @ApiModelProperty("计划实施时间")
    private Date planStartTime;

    @ApiModelProperty("计划完成时间")
    private Date planEndTime;

    @ApiModelProperty("实际结束时间")
    private Date actualEndTime;

    @ApiModelProperty("施工单位")
    private Integer constructionUnit;

    @ApiModelProperty("工程名称")
    private String projectName;

    @ApiModelProperty("迁改涉及网络层级")
    private String networkHierarchy;

    @ApiModelProperty("施工费（预算：元）")
    private String constructionBudget;

    @ApiModelProperty("甲供材料费(预算:元)")
    private String materialBudget;

    @ApiModelProperty("施工费(送审结算:元)")
    private String constructionCost;

    @ApiModelProperty("甲供材料费(送审结算:元)")
    private String materialCost;

    @ApiModelProperty("施工费审定金额(审计后:元)")
    private String constructionAuditCost;

    @ApiModelProperty("主动迁改或者被动")
    private Boolean isInitiative;

    @ApiModelProperty("项目类型(性质归类)")
    private String projectType;

    @ApiModelProperty("迁改原因")
    private String cause;

    @ApiModelProperty("对方单位")
    private String oppositeUnit;

    @ApiModelProperty("对方联系人")
    private String oppositeContacts;

    @ApiModelProperty("对方联系电话")
    private String oppositeContactsNum;

    @ApiModelProperty("有无赔补(0-没有、1-有)")
    private Boolean hasCompensation;

    @ApiModelProperty("被动补偿类型")
    private String compensationType;

    @ApiModelProperty("合同编号")
    private String contractNum;

    @ApiModelProperty("补偿合同名称")
    private String contractName;

    @ApiModelProperty("补偿金额")
    private String compensationAmount;

    @ApiModelProperty("预付款应付金额（元）")
    private Boolean anticipatePayable;

    @ApiModelProperty("预付款到账金额（元）")
    private String anticipatePayment;

    @ApiModelProperty("决算款到账金额（元）")
    private String finalPayment;

    @ApiModelProperty("补偿状态")
    private Integer compensationSate;

    @ApiModelProperty("未全额回款合同历时")
    private String contractDuration;

    @ApiModelProperty("赔补特殊情况备注")
    private String compensationRemake;

    @ApiModelProperty("月报")
    private String projectMonth;

    @ApiModelProperty("年份")
    private String projectYear;

    @ApiModelProperty("合同类型")
    private String contractType;
}
