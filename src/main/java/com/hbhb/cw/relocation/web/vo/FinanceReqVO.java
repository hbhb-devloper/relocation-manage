package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyk
 * @since 2020-10-9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceReqVO implements Serializable {

    private static final long serialVersionUID = -1608188188520678836L;

    @Schema(description ="区县")
    private Integer unitId;

    @Schema(description ="年份")
    private String year;

    @Schema(description ="项目类型")
    private String projectType;

    @Schema(description ="项目名称")
    private String projectName;

    @Schema(description ="计划完成时间")
    private String planEndTime;

    @Schema(description ="立项时间")
    private String projectTime;

    @Schema(description ="合同编号")
    private String contractNum;

    @Schema(description ="预付款是否已到账")
    private Integer receiptStatus;

}
