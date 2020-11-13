package com.hbhb.cw.relocation.mapper;


import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;

public interface FileMapper extends BaseMapper<RelocationFile> {

    List<Integer> selectFileByWarnId(Long warnId);
}