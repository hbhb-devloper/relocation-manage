package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResVO implements Serializable {

    private static final long serialVersionUID = -7894679294000895303L;

    @AutoID
    private Long id;
    @Schema(description = "区域")
    private String district;
    @Schema(description = "区域id")
    private Integer districtId;
    @Schema(description = "县市id")
    private Integer unitId;
    @Schema(description = "县市")
    private String unit;
    @Schema(description = "发票代码")
    private String invoiceCode;
    @Schema(description = "发票号码")
    private String invoiceNumber;
    @Schema(description = "开票点")
    private String invoiceSite;
    @Schema(description = "业务类型")
    private String businessType;
    @Schema(description = "发票类型")
    private String invoiceType;
    @Schema(description = "发票类型")
    private String invoiceTypeLabel;
    @Schema(description = "购方税号")
    private String buyerTax;
    @Schema(description = "购方名称")
    private String buyerName;
    @Schema(description = "开票项目")
    private String invoiceProject;
    @Schema(description = "开票日期")
    private String invoiceTime;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "税率")
    private BigDecimal taxRate;
    @Schema(description = "税额")
    private BigDecimal taxAmount;
    @Schema(description = "价税合计")
    private BigDecimal taxIncludeAmount;
    @Schema(description = "备注格式")
    private String remake;
    @Schema(description = "申请人")
    private String applicant;
    @Schema(description = "开票人")
    private String issuer;
    @Schema(description = "票据状态")
    private String state;
    @Schema(description = "是否为自定义菜单开票(0-否、1-是)")
    private String isImport;
    @Schema(description = "客户经理")
    private String manager;
    @Schema(description = "收款状态")
    private String paymentStatus;
    @Schema(description = "应收")
    private BigDecimal receivable;
    @Schema(description = "已收")
    private BigDecimal received;
    @Schema(description = "未收")
    private BigDecimal unreceived;

}
