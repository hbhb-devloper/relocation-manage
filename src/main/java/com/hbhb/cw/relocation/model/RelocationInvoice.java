package com.hbhb.cw.relocation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelocationInvoice implements Serializable {
    private static final long serialVersionUID = -2593276476905787755L;
    /**
     * id
     */
    private Long id;
    /**
     * 地区
     */
    private String district;
    /**
     * 县市（单位id）
     */
    private Integer unitId;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNumber;
    /**
     * 开票点
     */
    private String invoiceSite;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 发票类型
     */
    private Integer invoiceType;
    /**
     * 购方税号
     */
    private String buyerTax;
    /**
     * 购方名称
     */
    private String buyerName;
    /**
     * 开票项目
     */
    private String invoiceProject;
    /**
     * 开票日期
     */
    private Date invoiceTime;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 价税合计
     */
    private BigDecimal taxIncludeAmount;
    /**
     * 备注（合同号；区县；款项性质；项目信息；）
     */
    private String remake;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 开票人
     */
    private String issuer;
    /**
     * 票据状态
     */
    private Integer state;
    /**
     * 是否为自定义菜单开票(0-否、1-是)
     */
    private Integer isImport;
    /**
     * 客户经理
     */
    private String manager;
}
