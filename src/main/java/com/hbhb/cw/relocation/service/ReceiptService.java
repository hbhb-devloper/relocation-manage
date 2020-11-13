package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.web.vo.ReceiptExportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptImportVO;
import com.hbhb.cw.relocation.web.vo.ReceiptReqVO;
import com.hbhb.cw.relocation.web.vo.ReceiptResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

public interface ReceiptService {
    /**
     * 跟据条件查询收据列表并分页
     *
     * @param cond     条件
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 收据列表
     */
    PageResult<ReceiptResVO> getReceiptList(ReceiptReqVO cond, Integer pageNum, Integer pageSize);

    /**
     * 导入数据信息
     *
     * @param dataList 导入信息实体列表
     */
    void addSaveRelocationReceipt(List<ReceiptImportVO> dataList);


    /**
     * 跟据条件导出收据信息
     *
     * @param vo 查询条件
     * @return 列表
     */
    List<ReceiptExportVO> export(ReceiptReqVO vo);

    /**
     *  新增收据信息
     */
    void addRelocationReceipt(ReceiptResVO receiptResVO);

    /**
     * 修改收据信息
     */
    void updateRelocationReceipt(ReceiptResVO receiptResVO);

    /**
     * 根据id删除收据信息
     */
    void deleteReceipt(Long id);
}
