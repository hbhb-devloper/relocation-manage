package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import java.math.BigDecimal;
import java.util.List;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface IncomeMapper extends BaseMapper<RelocationIncome> {

    PageResult<IncomeResVO> getIncomeList(IncomeReqVO cond,
        PageRequest<IncomeResVO> request);

    void insertRelocationIncomeList(List<RelocationIncome> incomes);

    List<RelocationIncomeDetail> selectDetailById(
        Long id,
        Integer isNeed,
        String currentMonth);

    void updateIncomeDetail(RelocationIncomeDetail detail);

    void addIncomeDetail(RelocationIncomeDetail detail);

    List<IncomeExportVO> selectExportList(IncomeReqVO cond);

    BigDecimal getMonthAmount(Long id, String currentMonth);

    void updateIncomeUnreceived(Long id, BigDecimal amount);

    void updateIncomeReceived(Long id, BigDecimal amount);

    void updateIsReceived(Long id, int i);

    Long selectProject(String invoiceNum);
}