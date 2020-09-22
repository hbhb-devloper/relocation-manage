package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
public class ReceiptResVO implements Serializable {
    private static final long serialVersionUID = 9140346694492866779L;

    private Long id;
    @ApiModelProperty("类别")
    private String category;
    @ApiModelProperty("地区(单位)")
    private String unit;
    @ApiModelProperty("赔补金额")
    private BigDecimal compensationAmount;
    @ApiModelProperty("已到账金额")
    private BigDecimal paymentAmount;
    @ApiModelProperty("赔补合同名")
    private String contractName;
    @ApiModelProperty("合同编号")
    private String contractNum;
    @ApiModelProperty("赔补金额到账情况说明")
    private String paymentDesc;
    @ApiModelProperty("开收据金额")
    private BigDecimal receiptAmount;
    @ApiModelProperty("开收据时间")
    private Date receiptTime;
    @ApiModelProperty("备注格式")
    private String remake;
}
