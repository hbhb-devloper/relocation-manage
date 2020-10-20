package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.SqlResource;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface RelocationInvoiceMapper extends BaseMapper<RelocationInvoice> {

    PageResult<InvoiceResVO> selectListByCondition(InvoiceReqVO cond, PageRequest request);
}