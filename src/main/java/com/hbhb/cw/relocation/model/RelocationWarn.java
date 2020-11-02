package com.hbhb.cw.relocation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Long anticipatePayment;

    private Byte isReceived;

    private Long finalPayment;

    private Integer contractDuration;

    private Boolean state;

}