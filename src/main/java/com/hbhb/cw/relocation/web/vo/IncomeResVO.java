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
public class IncomeResVO implements Serializable {

    private static final long serialVersionUID = -1384092271081433149L;

    @AutoID
    private Long id;
    @Schema(description = "类别")
    private String category;
    @Schema(description = "经办单位(单位id)")
    private String unit;
    @Schema(description = "供应商")
    private String supplier;
    @Schema(description = "合同编号")
    private String contractNum;
    @Schema(description = "合同名称")
    private String contractName;
    @Schema(description = "起始时间")
    private String startTime;
    @Schema(description = "合同截止时间")
    private String contractDeadline;
    @Schema(description = "合同金额")
    private BigDecimal contractAmount;
    @Schema(description = "开票日期")
    private String invoiceTime;
    @Schema(description = "发票号码")
    private String invoiceNum;
    @Schema(description = "发票类型")
    private String invoiceType;
    @Schema(description = "发票类型值")
    private Integer invoiceTypeLabel;
    @Schema(description = "价款")
    private BigDecimal amount;
    @Schema(description = "税额")
    private BigDecimal tax;
    @Schema(description = "价格合计")
    private BigDecimal taxIncludeAmount;
    @Schema(description = "工程名")
    private String constructionName;
    @Schema(description = "收款情况")
    private String isReceived;
    @Schema(description = "账龄")
    private Integer aging;
    @Schema(description = "应收")
    private BigDecimal receivable;
    @Schema(description = "已收")
    private BigDecimal received;
    @Schema(description = "未收")
    private BigDecimal unreceived;
    @Schema(description = "款项类型")
    private String paymentType;
    @Schema(description = "收款单号")
    private String receiptNum;
    @Schema(description = "收款人")
    private String payee;
    @Schema(description = "本月已收款")
    private BigDecimal monthAmount;
}
