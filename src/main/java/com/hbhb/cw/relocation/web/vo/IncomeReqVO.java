package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;
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
public class IncomeReqVO implements Serializable {
    private static final long serialVersionUID = -1608188188520678836L;

    @ApiModelProperty("合同编号")
    private String contractNum;
    @ApiModelProperty("起始时间（最小）")
    private String startTimeFrom;
    @ApiModelProperty("起始时间（最大）")
    private String startTimeTo;
    @ApiModelProperty("合同截止时间（最小）")
    private String contractDeadlineFrom;
    @ApiModelProperty("合同截止时间（最大）")
    private String contractDeadlineTo;
    @ApiModelProperty("经办单位(单位id)")
    private Integer unitId;
    @ApiModelProperty("合同名称")
    private String contractName;
}
