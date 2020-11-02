package com.hbhb.cw.relocation.mapper;


import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import org.beetl.sql.mapper.BaseMapper;

import java.util.List;


public interface WarnMapper extends BaseMapper<RelocationWarn> {

    List<WarnResVO> selectProjectWarnByCond(WarnReqVO reqVO);

    List<String> selectProjectNum();

    void updateSateByProjectNum(List<String> list);
}