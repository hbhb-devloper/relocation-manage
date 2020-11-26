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
public class ContractFileVO implements Serializable {
    private static final long serialVersionUID = 705850265843676240L;

    private String contractNum;

    private Long fileId;
}
