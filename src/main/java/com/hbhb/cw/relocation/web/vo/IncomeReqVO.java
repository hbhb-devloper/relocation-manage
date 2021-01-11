package com.hbhb.cw.relocation.web.vo;

import com.hbhb.web.annotation.Decode;
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
public class IncomeReqVO implements Serializable {
    private static final long serialVersionUID = -1608188188520678836L;

    @Schema(description = "合同编号")
    @Decode
    private String contractNum;
    @Schema(description = "起始时间（最小）")
    private String startTimeFrom;
    @Schema(description = "起始时间（最大）")
    private String startTimeTo;
    @Schema(description = "合同截止时间（最小）")
    private String contractDeadlineFrom;
    @Schema(description = "合同截止时间（最大）")
    private String contractDeadlineTo;
    @Schema(description = "经办单位(单位id)")
    private Integer unitId;
    @Schema(description = "合同名称")
    private String contractName;
}
