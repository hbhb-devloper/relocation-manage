package com.hbhb.cw.relocation.service;


import com.hbhb.cw.relocation.web.vo.*;

import java.util.List;

public interface WarnService {

    /**
     * 根据条件查询预警信息
     */
    List<WarnResVO> getWarn(WarnReqVO reqVO);

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
     * @param fileVO 附件
     */
    void addWarnFile(WarnFileVO fileVO);

    /**
     * 跟据单位id统计预警数量
     */
    Integer getWarnCount(Integer unitId);

    /**
     * 跟据预警id获取预警附件列表
     * @param warnId 预警id
     * @return 附件列表
     */
    List<WarnFileResVO> getWarnFileList(Long warnId);
}
