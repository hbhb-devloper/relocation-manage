package com.hbhb.cw.relocation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelocationFile {
    private Long id;

    private Long warnId;

    private Date createTime;

    private String createBy;

    private Long fileId;

}