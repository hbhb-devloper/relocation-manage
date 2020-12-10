package com.hbhb.cw.relocation.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnFileVO implements Serializable {
    private static final long serialVersionUID = -4652154290191297717L;
    private Long fileId;
    private Long warnId;
}
