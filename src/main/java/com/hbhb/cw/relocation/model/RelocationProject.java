package com.hbhb.cw.relocation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dxk
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelocationProject implements Serializable {
    private static final long serialVersionUID = -4636882084219646443L;
    /**
     * id
     */
    private Long id;
    /**
     * 区域（单位id）
     */
    private Integer unitId;
    /**
     * 工程名称
     */
    private String projectName;
    /**
     * 项目编号
     */
    private String projectNum;
    /**
     * 项目类型（性质归类）
     */
    private String projectType;
    /**
     * 年份
     */
    private String projectYear;
    /**
     * 月报
     */
    private String projectMonth;
    /**
     * EOMS迁移修缮管理流程工单号
     */
    private String eomsRepairNum;
    /**
     * EOMS光缆割接流程工单号
     */
    private String eomsCutNum;
    /**
     * 计划实施时间
     */
    private Date planStartTime;
    /**
     * 计划完成时间
     */
    private Date planEndTime;
    /**
     * 实际结束时间
     */
    private Date actualEndTime;
    /**
     * 迁改涉及网络层级
     */
    private String networkHierarchy;
    /**
     * 施工费（预算：元）
     */
    private BigDecimal constructionBudget;
    /**
     * 施工费(送审结算:元)
     */
    private BigDecimal constructionCost;
    /**
     * 施工费审定金额(审计后:元)
     */
    private BigDecimal constructionAuditCost;
    /**
     * 施工单位
     */
    private Integer constructionUnit;
    /**
     * 甲供材料费(预算:元)
     */
    private BigDecimal materialBudget;
    /**
     * 甲供材料费(送审结算:元)
     */
    private BigDecimal materialCost;
    /**
     * 对方单位
     */
    private String oppositeUnit;
    /**
     * 对方联系人
     */
    private String oppositeContacts;
    /**
     * 对方联系电话
     */
    private String oppositeContactsNum;
    /**
     * 有无赔补(0-没有、1-有)
     */
    private Boolean hasCompensation;
    /**
     * 被动补偿类型
     */
    private String compensationType;
    /**
     * 补偿金额
     */
    private BigDecimal compensationAmount;
    /**
     * 补偿状态
     */
    private Integer compensationSate;
    /**
     * 赔补特殊情况备注
     */
    private String compensationRemake;
    /**
     * 合同编号
     */
    private String contractNum;
    /**
     * 合同类型
     */
    private String contractType;
    /**
     * 补偿合同名称
     */
    private String contractName;
    /**
     * 未全额回款合同历时
     */
    private Integer contractDuration;
    /**
     * 主动迁改或者被动
     */
    private Boolean isInitiative;
    /**
     * 预付款应付金额（元）
     */
    private BigDecimal anticipatePayable;
    /**
     * 预付款到账金额（元）
     */
    private BigDecimal anticipatePayment;
    /**
     * 决算款到账金额（元）
     */
    private BigDecimal finalPayment;
    /**
     * 迁改原因
     */
    private String cause;
}
