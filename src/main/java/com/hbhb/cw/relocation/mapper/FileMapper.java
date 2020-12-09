package com.hbhb.cw.relocation.mapper;


import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.model.RelocationFile;

import java.util.List;

public interface FileMapper extends BaseMapper<RelocationFile> {

    List<Integer> selectFileByWarnId(Long warnId);
}