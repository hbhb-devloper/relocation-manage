package com.hbhb.cw.relocation.mapper;


import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import com.hbhb.cw.relocation.web.vo.FinanceStatisticsVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.Param;

import java.util.List;

/**
 * @author hyk
 * @since 2020-10-9
 */
public interface FinanceMapper extends BaseMapper<FinanceResVO> {

    PageResult<FinanceResVO> getFinanceList(@Param("cond") FinanceReqVO cond,
                                            PageRequest<FinanceResVO> request);

    List<FinanceResVO> getFinanceList(FinanceReqVO cond);


    List<FinanceStatisticsVO> selectSumPayMonthAmount(String year);
}