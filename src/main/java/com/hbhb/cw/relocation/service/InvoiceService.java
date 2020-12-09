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
     * @param id id
     * @return 发票详情
     */
    RelocationInvoice getInvoiceDetail(Long id);

    /**
     * 修改发票信息
     *
     * @param invoice 发票内容
     */
    void updateInvoice(InvoiceResVO invoice);

    /**
     * 新增发票
     *
     * @param invoice 发票内容
     */
    void addInvoice(InvoiceResVO invoice);

    /**
     * 导入添加
     *
     * @param dataList excel导入
     */
    void addSaveRelocationInvoice(List<InvoiceImportVO> dataList);

    /**
     * 条件导出
     *
     * @param vo     条件
     * @param userId 用户id
     * @return 导出数据列表
     */
    List<InvoiceExportResVO> selectExportListByCondition(InvoiceReqVO vo, Integer userId);

    /**
     * 跟据发票编号查看发票详情
     *
     * @param invoiceNum 发票编号
     * @return 发票详情
     */
    RelocationInvoice getInvoice(String invoiceNum);

    /**
     * 检测文件类型
     *
     * @param fileName 文件类型
     */
    void judgeFileName(String fileName);
}
