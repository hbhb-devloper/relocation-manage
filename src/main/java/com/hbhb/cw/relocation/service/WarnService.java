package com.hbhb.cw.relocation.service;



import com.hbhb.cw.relocation.web.vo.WarnExportVO;
import com.hbhb.cw.relocation.web.vo.WarnFileVO;
import com.hbhb.cw.relocation.web.vo.WarnReqVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
import com.hbhb.cw.systemcenter.vo.FileDetailVO;

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
}
