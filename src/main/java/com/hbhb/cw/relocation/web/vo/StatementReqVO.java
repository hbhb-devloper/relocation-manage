package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementReqVO implements Serializable {

    private static final long serialVersionUID = -3743119724401570377L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "父类id")
    private Integer parentId;
}
