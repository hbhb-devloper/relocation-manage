package com.hbhb.cw.relocation.mapper;


import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.web.beetlsql.BaseMapper;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;


public interface WarnMapper extends BaseMapper<RelocationWarn> {

    List<WarnResVO> selectProjectWarnByCond(WarnReqVO cond);

    List<String> selectProjectNum();

    void updateSateByProjectNum(List<String> list);

    int selectWarnCountByUnitId(Integer unitId);

    PageResult<WarnResVO> selectWarnListByCond(WarnReqVO cond, PageRequest<WarnResVO> request);
}