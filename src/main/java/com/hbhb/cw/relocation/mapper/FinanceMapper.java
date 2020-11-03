package com.hbhb.cw.relocation.mapper;


import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.web.beetlsql.BaseMapper;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author hyk
 * @since 2020-10-9
 */
public interface FinanceMapper extends BaseMapper<FinanceResVO> {

    PageResult<FinanceResVO> getFinanceList(FinanceReqVO cond,
        PageRequest<FinanceResVO> request);

    List<FinanceResVO> getFinanceList(FinanceReqVO cond);
}