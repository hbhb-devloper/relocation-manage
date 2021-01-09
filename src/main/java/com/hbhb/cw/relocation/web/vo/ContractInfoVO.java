package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractInfoVO implements Serializable {
    private static final long serialVersionUID = 3168616258670505775L;
    @Schema(description = "合同编号")
    private String contractNum;
    @Schema(description = "合同名称")
    private String contractName;
    @Schema(description = "计划完成时间(最小)")
    private Date planStartTime;
    @Schema(description = "计划完成时间最大")
    private Date planEndTime;
    @Schema(description = "赔补金额合计")
    private BigDecimal total;

}
