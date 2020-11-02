package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationReceipt;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import org.beetl.sql.mapper.BaseMapper;

import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
public interface ReceiptMapper extends BaseMapper<RelocationReceipt> {

    List<ReceiptResVO> selectReceiptByCond(ReceiptReqVO cond);
}