package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.cw.relocation.web.vo.ProjectInfoVO;
import com.hbhb.web.beetlsql.BaseMapper;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface InvoiceMapper extends BaseMapper<RelocationInvoice> {

    PageResult<InvoiceResVO> selectListByCondition(InvoiceReqVO cond, PageRequest request);

    List<ProjectInfoVO> getProjectInfo();

    List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO cond);

    Long selectPidByCondition(String contractNum, Integer unitId, String pinfo);

    List<String> selectInvoiceRemake();

}