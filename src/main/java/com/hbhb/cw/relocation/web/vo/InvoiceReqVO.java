package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class InvoiceReqVO implements Serializable {

    private static final long serialVersionUID = 4590998101269572056L;

    @Schema(description = "区域（单位id）")
    private Integer unitId;

    @Schema(description = "开票日期（开始）")
    private String invoiceTimeFrom;

    @Schema(description = "开票日期（结束）")
    private String invoiceTimeTo;

    @Schema(description = "金额（最小）")
    private BigDecimal amountFrom;

    @Schema(description = "金额（最大）")
    private BigDecimal amountTo;


}
