package com.hbhb.cw.relocation.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarnCountVO implements Serializable {
    private static final long serialVersionUID = -1124249820824938345L;

    private Integer unitId;

    private Integer count;
}
