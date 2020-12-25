package com.hbhb.cw.relocation.web.vo;

import com.hbhb.web.annotation.Decode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnReqVO implements Serializable {
    private static final long serialVersionUID = 7093102210152316125L;

    @Schema(description = "未全额回款历时")
    private String contractDuration;

    @Schema(description = "合同编号")
    @Decode
    private String contractNum;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "单位")
    private Integer unitId;

    @Schema(description = "预警类型")
    private Integer type;

}
