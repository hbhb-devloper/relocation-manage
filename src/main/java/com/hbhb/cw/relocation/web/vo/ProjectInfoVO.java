package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoVO {

    private static final long serialVersionUID = -1608188188520678836L;

    @Schema(description = "项目信息")
    private String info;

    @Schema(description = "项目id")
    private Long id;

}
