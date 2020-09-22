package com.hbhb.cw.relocation.web.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("合同编号")
    private String contractNum;

    @ApiModelProperty("区域（单位id）")
    private Integer unitId;

    @ApiModelProperty("项目编号")
    private String projectNum;

    @ApiModelProperty("补偿状态")
    private Integer compensationSate;

    @ApiModelProperty("未全额回款合同历时")
    private String contractDuration;

    @ApiModelProperty("工程名称")
    private String projectName;
}
