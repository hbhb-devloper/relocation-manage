package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceStatisticsVO implements Serializable {
    private static final long serialVersionUID = -4051705266560148566L;

    @Schema(description = "合同编号")
    private String contractNum;

    @Schema(description = "1月收款")
    private BigDecimal janReceivable;

    @Schema(description = "2月收款")
    private BigDecimal febReceivable;

    @Schema(description = "3月收款")
    private BigDecimal marReceivable;

    @Schema(description = "4月收款")
    private BigDecimal aprReceivable;

    @Schema(description = "5月收款")
    private BigDecimal mayReceivable;

    @Schema(description = "6月收款")
    private BigDecimal juneReceivable;

    @Schema(description = "7月收款")
    private BigDecimal julReceivable;

    @Schema(description = "8月收款")
    private BigDecimal augReceivable;

    @Schema(description = "9月收款")
    private BigDecimal sepReceivable;

    @Schema(description = "10月收款")
    private BigDecimal octReceivable;

    @Schema(description = "11月收款")
    private BigDecimal novReceivable;

    @Schema(description = "12月收款")
    private BigDecimal decReceivable;

    @Schema(description = "待回收金额")
    private BigDecimal unpaidCollection;

    @Schema(description = "初始化回收金额【2019年底】")
    private BigDecimal initRecoveredAmount;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;
}
