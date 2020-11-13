package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationReceipt;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import com.hbhb.web.beetlsql.BaseMapper;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface ReceiptMapper extends BaseMapper<RelocationReceipt> {

    PageResult<ReceiptResVO>selectReceiptByCond(ReceiptReqVO cond, PageRequest<ReceiptResVO> request);

    List<ReceiptResVO>  selectReceiptListByCond(ReceiptReqVO cond);
}