package com.hbhb.cw.relocation.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface InvoiceMapper extends BaseMapper<RelocationInvoice> {

    PageResult<InvoiceResVO> selectListByCondition(InvoiceReqVO cond, PageRequest request);

    List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO cond);

    Integer selectListByNumber(String invoiceNumber);

    List<String> selectInvoiceNumber();

    RelocationInvoice selectInvoiceByInvoiceNum(String invoiceNum);


}