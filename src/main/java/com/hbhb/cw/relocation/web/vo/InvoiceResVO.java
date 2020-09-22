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
public class InvoiceResVO implements Serializable {
    private static final long serialVersionUID = -7894679294000895303L;

    private Long id;
    @ApiModelProperty("区域")
    private String district;
    @ApiModelProperty("县市")
    private String unit;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNumber;
    @ApiModelProperty("开票点")
    private String invoiceSite;
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("购方税号")
    private String buyerTax;
    @ApiModelProperty("购方名称")
    private String buyerName;
    @ApiModelProperty("开票项目")
    private String invoiceProject;
    @ApiModelProperty("开票日期")
    private Date invoiceTime;
    @ApiModelProperty("金额")
    private BigDecimal amount;
    @ApiModelProperty("税率")
    private BigDecimal taxRate;
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;
    @ApiModelProperty("价税合计")
    private BigDecimal taxIncludeAmount;
    @ApiModelProperty("备注格式")
    private String remake;
    @ApiModelProperty("申请人")
    private String applicant;
    @ApiModelProperty("开票人")
    private String issuer;
    @ApiModelProperty("票据状态")
    private String state;
    @ApiModelProperty("是否为自定义菜单开票(0-否、1-是)")
    private Integer isImport;
    @ApiModelProperty("客户经理")
    private String manager;
}
