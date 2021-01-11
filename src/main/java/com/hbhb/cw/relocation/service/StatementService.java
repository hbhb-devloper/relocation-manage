package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.StatementExportVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

public interface StatementService {
    /**
     * 跟据单位id查询迁改统计列表
     */
    PageResult<StatementResVO> getStatementList(Integer pageNum, Integer pageSize, Integer unitId, Integer userId);

    /**
     * 导出迁改统计信息
     */
    List<StatementExportVO> export(Integer unitId);
}
