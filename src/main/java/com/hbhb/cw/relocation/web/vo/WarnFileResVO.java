package com.hbhb.cw.relocation.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarnFileResVO  implements Serializable {
    private static final long serialVersionUID = -6472705963910006874L;

    @Schema(description = "附件id")
    private Long fileId;

    @Schema(description = "附件路径")
    private String  filepath;

    @Schema(description = "附件名称")
    private String fileName;

}
