package com.hbhb.cw.relocation.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSelectVO implements Serializable {
    private static final long serialVersionUID = 8716582677047370236L;

    private String num;

    private BigDecimal account;
}
