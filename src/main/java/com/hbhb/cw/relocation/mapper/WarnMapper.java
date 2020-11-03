package com.hbhb.cw.relocation.mapper;


import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.vo.SelectVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;


public interface WarnMapper extends BaseMapper<RelocationWarn> {

    List<WarnResVO> selectProjectWarnByCond(WarnReqVO reqVO);

    List<String> selectProjectNum();

    void updateSateByProjectNum(List<SelectVO> list);
}