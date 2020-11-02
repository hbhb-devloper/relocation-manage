package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnResVO implements Serializable {
    private static final long serialVersionUID = -82461158691671865L;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "市区")
    private String unitName;

    @Schema(description = "施工单位")
    private String constructionUnit;

    @Schema(description = "对方单位")
    private String oppositeUnit;

    @Schema(description = "合同编号")
    private String contractNum;

    @Schema(description = "预付款到账金额")
    private String anticipatePayment;

    @Schema(description = "是否已经收款")
    private String isReceived;

    @Schema(description = "决算款到账金额（元）")
    private String finalPayment;

    @Schema(description = "未全额回款合同历时")
    private String contractDuration;

}
