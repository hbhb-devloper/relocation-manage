package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.springboot.web.view.Page;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
public interface InvoiceService {

    /**
     * 分页获取发票列表
     */
    Page<InvoiceResVO> getInvoiceList(long pageNum, long pageSize, InvoiceReqVO cond);
}
