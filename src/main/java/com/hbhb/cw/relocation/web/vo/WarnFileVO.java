package com.hbhb.cw.relocation.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarnFileVO implements Serializable {
    private static final long serialVersionUID = 6520545005680577492L;

    @Schema(description = "预警id")
    private Long warnId;

    @Schema(description = "附件id")
    private Long fileId;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;
}
