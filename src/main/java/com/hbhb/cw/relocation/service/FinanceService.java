package com.hbhb.cw.relocation.service;


import com.hbhb.cw.relocation.web.vo.FinanceReqVO;
import com.hbhb.cw.relocation.web.vo.FinanceResVO;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.beetl.sql.core.page.PageResult;

/**
 * @author hyk
 * @since 2020-10-9
 */

public interface FinanceService {

    PageResult<FinanceResVO> getFinanceList(Integer pageNum, Integer pageSize,
        FinanceReqVO cond, Integer userId) throws UnsupportedEncodingException;

    List<FinanceResVO> selectExportListByCondition(FinanceReqVO cond, Integer userId);

}
