package com.hbhb.cw.relocation.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface IncomeMapper extends BaseMapper<RelocationIncome> {

    PageResult<IncomeResVO> getIncomeList(IncomeReqVO cond, PageRequest<IncomeResVO> request);

    List<RelocationIncomeDetail> selectDetailById(Long id, Integer isNeed, String currentMonth);

    List<IncomeExportVO> selectExportList(IncomeReqVO cond);

    BigDecimal getMonthAmount(Long id, String currentMonth);

    Long selectProject(String invoiceNum);
}