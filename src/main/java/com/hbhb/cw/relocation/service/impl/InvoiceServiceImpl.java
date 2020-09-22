package com.hbhb.cw.relocation.service.impl;

import com.hbhb.cw.relocation.mapper.RelocationInvoiceMapper;
import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.service.InvoiceService;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import com.hbhb.springboot.web.view.Page;

import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

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
    public Page<InvoiceResVO> getInvoiceList(long pageNum, long pageSize, InvoiceReqVO cond) {
        PageQuery<RelocationInvoice> query = new PageQuery<>(pageNum, pageSize);
        relocationInvoiceMapper.templatePage(query);

        List<InvoiceResVO> result = new ArrayList<>();
        List<RelocationInvoice> list = query.getList();
        list.forEach(l -> {
            InvoiceResVO vo = new InvoiceResVO();
            BeanUtils.copyProperties(l, vo);
            result.add(vo);
        });

        return new Page<>(result, query.getTotalRow());
    }
}
