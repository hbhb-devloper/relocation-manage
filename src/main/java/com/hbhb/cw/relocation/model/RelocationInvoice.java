package com.hbhb.cw.relocation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelocationInvoice implements Serializable {

    private static final long serialVersionUID = -2593276476905787755L;
    @AutoID
    private Long id;

    @Schema(description = "区县")
    private Integer district;

    @Schema(description = "市县")
    private Integer unitId;

    @Schema(description = "发票代码")
    private String invoiceCode;

    @Schema(description = "发票号码")
    private String invoiceNumber;

    @Schema(description = "开票点")
    private String invoiceSite;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "发票类型")
    private Integer invoiceType;

    @Schema(description = "购方税号")
    private String buyerTax;

    @Schema(description = "购方名称")
    private String buyerName;

    @Schema(description = "开票项目")
    private String invoiceProject;

    @Schema(description = "开票日期")
    private Date invoiceTime;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "税额")
    private BigDecimal taxAmount;

    @Schema(description = "价税合计")
    private BigDecimal taxIncludeAmount;

    @Schema(description = "备注")
    private String remake;

    @Schema(description = "描述")
    private String describe;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "开票人")
    private String issuer;

    @Schema(description = "票据状态1:蓝字0:红字")
    private Integer state;

    @Schema(description = "是否为自定义菜单开票1:是0:否")
    private Integer isImport;

    @Schema(description = "客户经理")
    private String manager;

    @Schema(description = "款项类型")
    private Integer paymentType;

    @Schema(description = "所属项目名")
    private Long projectId;

}
