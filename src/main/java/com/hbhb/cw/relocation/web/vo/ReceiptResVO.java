package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResVO implements Serializable {
    private static final long serialVersionUID = 9140346694492866779L;

    private Long id;
    @Schema(description = "类别")
    private String category;
    @Schema(description = "地区(单位)id")
    private Integer unitId;
    @Schema(description = "地区(单位)id")
    private String unitName;
    @Schema(description = "收据编号")
    private String receiptNum;
    @Schema(description = "赔补金额")
    private String compensationAmount;
    @Schema(description = "已到账金额")
    private String paymentAmount;
    @Schema(description = "赔补合同名")
    private String contractName;
    @Schema(description = "合同编号")
    private String contractNum;
    @Schema(description = "赔补金额到账情况说明")
    private String paymentDesc;
    @Schema(description = "开收据金额")
    private String receiptAmount;
    @Schema(description = "开收据时间")
    private String receiptTime;
    @Schema(description = "备注格式")
    private String remake;
}
