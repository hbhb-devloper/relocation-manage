package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wxg
 * @since 2020-09-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementResVO implements Serializable {
    private static final long serialVersionUID = -6822522686097721065L;

    @Schema(description = "区域id")
    private Integer unitId;

    @Schema(description = "区域")
    private String unitName;

    @Schema(description = "有赔迁改项目数量（个）")
    private Integer compensationAmount;

    @Schema(description = "已签订赔补协议项目数")
    private Integer contractNumAmount;

    @Schema(description = "未签订赔补协议项目数")
    private Integer notContractNumAmount;

    @Schema(description = "赔补合同签订率(保留两位小数)")
    private String compensationRatio;

    @Schema(description = "签订赔补协议数量（个）")
    private Integer contractAmount;

    @Schema(description = "签订赔补协金额（万元)")
    private BigDecimal contractAccount;

    @Schema(description = "未签订赔补协议项目成本金额（万元")
    private BigDecimal notContractAccount;

    @Schema(description = "赔补款累计到账金额（万元)")
    private BigDecimal compensationTotal;

    @Schema(description = "有赔迁改项目累计成本金额（万元)")
    private BigDecimal costTotal;

    @Schema(description = "回款成本收支比")
    private String costRation;

    @Schema(description = "预付款未收金额（万元)")
    private BigDecimal budgetNotAccount;

    @Schema(description = "尾款未收金额")
    private BigDecimal finalNotPayment;

    @Schema(description = "未全额回款合同1年内（个）")
    private Integer oneNotCostAmount;

    @Schema(description = "未全额回款合同1-3年（个）")
    private Integer twoNotCostAmount;

    @Schema(description = "未全额回款合同3年以上（个)")
    private Integer threeNotCostAmount;

    @Schema(description = "当年赔补开票（万元）")
    private BigDecimal thisYearInvoiceAccount;
}
