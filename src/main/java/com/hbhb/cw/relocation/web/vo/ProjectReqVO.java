package com.hbhb.cw.relocation.web.vo;

import com.hbhb.web.annotation.Decode;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaokang
 * @since 2020-09-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReqVO implements Serializable {
    private static final long serialVersionUID = 1660341686428378078L;

    @Schema(description = "合同编号")
    @Decode
    private String contractNum;

    @Schema(description = "区域（单位id）")
    private Integer unitId;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "补偿状态")
    private Integer compensationSate;

    @Schema(description = "未全额回款合同历时")
    private String contractDuration;

    @Schema(description = "工程名称")
    private String projectName;
}
