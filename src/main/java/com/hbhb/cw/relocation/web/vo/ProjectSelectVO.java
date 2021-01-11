package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSelectVO implements Serializable {
    private static final long serialVersionUID = 8716582677047370236L;

    @Schema(description = "合同编号")
    private String num;

    @Schema(description = "施工费总额")
    private BigDecimal constructionBudget;

    @Schema(description = "预付款应付金额总额")
    private BigDecimal anticipatePayable;

    @Schema(description = "预付款到账金额总额")
    private BigDecimal anticipatePayment;

    @Schema(description = "决算款到账总额")
    private BigDecimal finalPayment;

    @Schema(description = "赔补总额")
    private BigDecimal compensationAmount;

}
