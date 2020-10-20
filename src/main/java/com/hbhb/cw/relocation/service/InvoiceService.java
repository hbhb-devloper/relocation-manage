package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;

import org.beetl.sql.core.page.PageResult;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
public interface InvoiceService {

    /**
     * 分页获取发票列表
     */
    PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize, InvoiceReqVO cond);
}
