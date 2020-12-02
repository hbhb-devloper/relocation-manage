package com.hbhb.cw.relocation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

/**
 * @author dxk
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelocationIncome implements Serializable {

    private static final long serialVersionUID = 4860108395368299254L;
    /**
     * id
     */
    @AutoID
    private Long id;
    /**
     * 类别
     */
    private Integer category;
    /**
     * 经办单位(单位id)
     */
    private Integer unitId;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 合同编号
     */
    private String contractNum;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 起始时间
     */
    private Date startTime;
    /**
     * 合同截止时间
     */
    private Date contractDeadline;
    /**
     * 合同金额
     */
    private BigDecimal contractAmount;
    /**
     * 开票时间
     */
    private Date invoiceTime;
    /**
     * 发票号码
     */
    private String invoiceNum;
    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 价款
     */
    private BigDecimal amount;
    /**
     * 税额
     */
    private BigDecimal tax;
    /**
     * 价格合计
     */
    private BigDecimal taxIncludeAmount;
    /**
     * 工程名
     */
    private String constructionName;
    /**
     * 款项类型
     */
    private Integer paymentType;
    /**
     * 收款情况（0-未收款、1-已收款）
     */
    private Integer isReceived;
    /**
     * 账龄
     */
    private Integer aging;
    /**
     * 应收
     */
    private BigDecimal receivable;
    /**
     * 已收
     */
    private BigDecimal received;
    /**
     * 未收
     */
    private BigDecimal unreceived;
    /**
     * 收款单号
     */
    private String receiptNum;
    /**
     * 收款人
     */
    private String payee;

    /**
     * 账龄分类
     */
    private String amountType;
    /**
     * 计提编号
     */
    private String accrualNumber;
}
