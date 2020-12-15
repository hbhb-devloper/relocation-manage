package com.hbhb.cw.relocation.service;

import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author hyk
 * @since 2020-09-27
 */

public interface IncomeService {

    /**
     * 按照条件查询收款列表
     *
     * @param pageNum  页 数
     * @param pageSize 条数
     * @param cond     条件
     * @param userId   用户id
     * @return 收款列表
     */
    PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize, IncomeReqVO cond, Integer userId);

    /**
     * 按照收款查询收款详情列表
     *
     * @param id     id
     * @param isNeed 是否为本月
     * @return 详情列表
     */
    List<RelocationIncomeDetail> getIncomeDetail(Long id, Integer isNeed);

    /**
     * 修改收款详情
     *
     * @param detail 详情信息
     */
    void updateIncomeDetail(RelocationIncomeDetail detail);

    /**
     * 新增收款详情
     *
     * @param detail 收款详情
     * @param userId 用户id
     */
    void addIncomeDetail(RelocationIncomeDetail detail, Integer userId);

    /**
     * 导入收款信息
     *
     * @param dataList      导入
     * @param importHeadMap 表头
     */
    void addSaveRelocationInvoice(List<IncomeImportVO> dataList, Map<Integer, String> importHeadMap);

    /**
     * 跟据条件导出收款列表
     *
     * @param vo     条件
     * @param userId 用户id
     * @return 收款列表
     */
    List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo, Integer userId);

    /**
     * 导入文件格式名
     *
     * @param fileName 格式名称
     */
    void judgeFileName(String fileName);

}
