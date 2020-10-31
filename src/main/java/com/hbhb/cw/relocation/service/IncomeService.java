package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import java.util.List;
import org.beetl.sql.core.page.PageResult;

/**
 * @author hyk
 * @since 2020-09-27
 */

public interface IncomeService {

    void judgeFileName(String fileName);

    PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize, IncomeReqVO cond,
        Integer userId);

    List<RelocationIncomeDetail> getIncomeDetail(Long id, Integer isNeed);

    void updateIncomeDetail(RelocationIncomeDetail detail);

    void addIncomeDetail(RelocationIncomeDetail detail, Integer userId);

    void addSaveRelocationInvoice(List<IncomeImportVO> dataList);

    List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo, Integer userId);

}
