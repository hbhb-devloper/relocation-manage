package com.hbhb.cw.relocation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelocationWarn {
    private Long id;

    private String projectNum;

    private Integer unitId;

    private String constructionUnit;

    private String oppositeUnit;

    private String contractNum;

    private BigDecimal anticipatePayment;

    private Boolean isReceived;

    private BigDecimal finalPayment;

    private Integer contractDuration;

    private Boolean state;

    private Long projectId;

    private Integer compensationSate;

}