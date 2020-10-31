package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.cw.relocation.web.vo.ProjectInfoVO;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import java.util.List;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface InvoiceMapper extends BaseMapper<RelocationInvoice> {

    PageResult<InvoiceResVO> selectListByCondition(InvoiceReqVO cond, PageRequest request);

    RelocationInvoice getInvoiceDetailById(Long id);

    void updateByPrimaryKey(RelocationInvoice invoice);

    List<ProjectInfoVO> getProjectInfo();

    List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO cond);

    Long selectPidByCondition(String contractNum, Integer unitId, String pinfo);
}