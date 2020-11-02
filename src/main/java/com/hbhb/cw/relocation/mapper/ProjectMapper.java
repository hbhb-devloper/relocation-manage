package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.cw.systemcenter.vo.SelectVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author dxk
 * @since 2020-09-22
 */
@SqlResource("relocationProject")
public interface ProjectMapper extends BaseMapper<RelocationProject> {

    PageResult<ProjectResVO> selectProjectByCond(ProjectReqVO cond, PageRequest<ProjectResVO> request);

    List<String> selectProjectNum();

    List<String> selectContractNumList();

    PageResult<StatementResVO> selectProjectStatementByUnitId(Integer unitId, PageRequest<ProjectResVO> request);

    List<AmountVO> selectCompensationAmount(List<String> contractNumNewList);

    Map<String, BigDecimal> selectSumConstructionBudget(List<String> contractNumNewList);

    void updateBatch(List<AmountVO> relocation);

    List<RelocationProject> selectProject();

    void updateBatchDuration(List<SelectVO> list);

    Map<String, BigDecimal> selectSumCompensationAmount();

    List<String> selectProjectNumByProjectNum(List<String> _root);

    List<WarnResVO> selectProjectWarn();

}