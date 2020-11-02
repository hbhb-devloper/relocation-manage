package com.hbhb.cw.relocation.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountVO implements Serializable {
    private static final long serialVersionUID = 3966285521642038043L;
    @Schema(description ="id")
    private Long id;

    @Schema(description ="合同编号")
    private String contractNum;

    @Schema(description ="施工费（预算：元）")
    private BigDecimal constructionBudget;

    @Schema(description ="补偿金额")
    private BigDecimal compensationAmount;

}
