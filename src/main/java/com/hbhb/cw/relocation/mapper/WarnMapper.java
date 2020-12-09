package com.hbhb.cw.relocation.mapper;


import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.model.RelocationWarn;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.Update;

import java.util.List;


public interface WarnMapper extends BaseMapper<RelocationWarn> {

    List<WarnResVO> selectProjectWarnByCond(WarnReqVO cond);

    List<String> selectProjectNum();

    @Update
    void updateSateByProjectNum(List<String> list);

    int selectWarnCountByUnitId(Integer unitId);

    PageResult<WarnResVO> selectWarnListByCond(WarnReqVO cond, PageRequest<WarnResVO> request);
}