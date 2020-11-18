package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.model.RelocationInvoice;
import com.hbhb.cw.relocation.web.vo.InvoiceExportResVO;
import com.hbhb.cw.relocation.web.vo.InvoiceImportVO;
import com.hbhb.cw.relocation.web.vo.InvoiceReqVO;
import com.hbhb.cw.relocation.web.vo.InvoiceResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
public interface InvoiceService {

    /**
     * 分页获取发票列表
     */
    PageResult<InvoiceResVO> getInvoiceList(Integer pageNum, Integer pageSize, InvoiceReqVO cond,
        Integer userId);

    /**
     * 发票详情
     *
     * @param id
     * @return
     */
    RelocationInvoice getInvoiceDetail(Long id);

    /**
     * 修改发票信息
     *
     * @param invoice
     */
    void updateInvoice(InvoiceResVO invoice);

    /**
     * 新增发票
     *
     * @param invoice
     */
    void addInvoice(InvoiceResVO invoice);

    /**
     * 检测文件类型
     *
     * @param fileName
     */
    void judgeFileName(String fileName);

    /**
     * 导入添加
     *
     * @param dataList
     */
    void addSaveRelocationInvoice(List<InvoiceImportVO> dataList);

    /**
     * 条件导出
     *
     * @param vo
     * @param userId
     * @return
     */
    List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId);
}
