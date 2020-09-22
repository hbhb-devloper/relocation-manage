package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class ReceiptReqVO implements Serializable {
    private static final long serialVersionUID = 4283560339034632797L;

    @ApiModelProperty("区域（单位id）")
    private Integer unitId;

    @ApiModelProperty("开票日期（开始）")
    private String invoiceTimeFrom;

    @ApiModelProperty("开票日期（结束）")
    private String invoiceTimeTo;

    @ApiModelProperty("金额（最小）")
    private BigDecimal amountFrom;

    @ApiModelProperty("金额（最大）")
    private BigDecimal amountTo;
}
