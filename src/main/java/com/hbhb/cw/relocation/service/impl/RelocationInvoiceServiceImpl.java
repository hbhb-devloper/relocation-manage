package com.hbhb.cw.relocation.service.impl;

import com.hbhb.cw.relocation.mapper.RelocationInvoiceMapper;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.service.RelocationInvoiceService;
import com.hbhb.springboot.web.view.Page;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;

import org.beetl.sql.core.engine.PageQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
@Service
@Slf4j
public class RelocationInvoiceServiceImpl implements RelocationInvoiceService {

    @Resource
    private RelocationInvoiceMapper relocationInvoiceMapper;

    @Override
    public Page<RelocationInvoice> getInvoiceList(long pageNum, long pageSize, InvoiceReqVO cond) {
        PageQuery<RelocationInvoice> query = new PageQuery<>(pageNum, pageSize);
        relocationInvoiceMapper.templatePage(query);
        return new Page<>(query.getList(), query.getTotalRow());
    }
}
