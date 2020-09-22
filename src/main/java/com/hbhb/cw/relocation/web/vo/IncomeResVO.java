package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
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
public class IncomeResVO implements Serializable {
    private static final long serialVersionUID = -1384092271081433149L;

    private Long id;
    @ApiModelProperty("类别")
    private String category;
    @ApiModelProperty("经办单位(单位id)")
    private String unit;
    @ApiModelProperty("供应商")
    private String supplier;
    @ApiModelProperty("合同编号")
    private String contractNum;
    @ApiModelProperty("合同名称")
    private String contractName;
    @ApiModelProperty("起始时间")
    private Date startTime;
    @ApiModelProperty("合同截止时间")
    private Date contractDeadline;
    @ApiModelProperty("合同金额")
    private BigDecimal contractAmount;
    @ApiModelProperty("开票日期")
    private Date invoiceTime;
    @ApiModelProperty("发票号码")
    private String invoiceNum;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("价款")
    private BigDecimal amount;
    @ApiModelProperty("税额")
    private BigDecimal tax;
    @ApiModelProperty("价格合计")
    private BigDecimal taxIncludeAmount;
    @ApiModelProperty("工程名")
    private String constructionName;
    @ApiModelProperty("收款情况")
    private String isReceived;
    @ApiModelProperty("账龄")
    private Integer aging;
    @ApiModelProperty("应收")
    private BigDecimal receivable;
    @ApiModelProperty("已收")
    private BigDecimal received;
    @ApiModelProperty("未收")
    private BigDecimal unreceived;
    @ApiModelProperty("款项类型")
    private Integer paymentType;
    @ApiModelProperty("收款单号")
    private String receiptNum;
    @ApiModelProperty("收款人")
    private String payee;
}
