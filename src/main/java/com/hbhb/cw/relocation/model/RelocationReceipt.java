package com.hbhb.cw.relocation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dxk
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelocationReceipt implements Serializable {
    private static final long serialVersionUID = -2771050419944956043L;
    /**
     * id
     */
    private Long id;
    /**
     * 类别
     */
    private String category;
    /**
     * 地区(单位id)
     */
    private Integer unitId;
    /**
     * 赔补金额
     */
    private BigDecimal compensationAmount;
    /**
     * 已到账金额
     */
    private BigDecimal paymentAmount;
    /**
     * 赔补合同名
     */
    private String contractName;
    /**
     * 合同编号
     */
    private String contractNum;
    /**
     * 赔补金额到账情况说明
     */
    private String paymentDesc;
    /**
     * 开收据金额
     */
    private BigDecimal receiptAmount;
    /**
     * 开收据时间
     */
    private Date receiptTime;
    /**
     * 备注（合同号；区县；款项性质；项目信息；）
     */
    private String remake;
}
