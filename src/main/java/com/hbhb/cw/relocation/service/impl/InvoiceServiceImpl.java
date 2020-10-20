package com.hbhb.cw.relocation.service.impl;

import com.hbhb.cw.relocation.mapper.RelocationInvoiceMapper;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    @Resource
    private RelocationInvoiceMapper relocationInvoiceMapper;

    @Override
    public PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize,
        InvoiceReqVO cond) {
        PageRequest<InvoiceResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        return relocationInvoiceMapper.selectListByCondition(cond,request);
    }
}
