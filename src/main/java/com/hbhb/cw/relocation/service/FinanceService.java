package com.hbhb.cw.relocation.service;


import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author hyk
 * @since 2020-10-9
 */

public interface FinanceService {
    /**
     * 跟据条件查询涉财统计列表
     *
     * @param pageNum  页数
     * @param pageSize 条数
     * @param cond     条件
     * @param userId   用户id
     * @return 涉财统计列表
     */
    PageResult<FinanceResVO> getFinanceList(Integer pageNum, Integer pageSize, FinanceReqVO cond, Integer userId);

    /**
     * 按照条件导出涉财统计列表
     *
     * @param cond   条件
     * @param userId 用户id
     * @return 导出列表
     */
    List<FinanceResVO> selectExportListByCondition(FinanceReqVO cond, Integer userId);

}
