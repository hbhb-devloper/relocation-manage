package com.hbhb.cw.relocation.service;


import com.hbhb.cw.relocation.model.RelocationFile;
import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileResVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

public interface WarnService {

    /**
     * 根据条件查询预警信息
     */
    List<WarnResVO> getWarn(WarnReqVO reqVO, Integer userId);

    /**
     * 根据条件导出预警信息
     */
    List<WarnExportVO> export(WarnReqVO reqVO);

    /**
     * 同步预警信息
     */
    void addSaveWarn();

    /**
     * 新增预警附件
     */
    void addWarnFile(RelocationFile relocationFile);

    /**
     * 跟据用户id统计预警数量
     */
    Long getWarnCount(Integer userId);

    /**
     * 跟据预警id获取预警附件列表
     *
     * @param warnId 预警id
     * @return 附件列表
     */
    List<WarnFileResVO> getWarnFileList(Long warnId);

    /**
     * 根据条件查询工作台信息
     */
    PageResult<WarnResVO> getWarnList(WarnReqVO warnReqVO, Integer userId, Integer pageNum, Integer pageSize);
}
