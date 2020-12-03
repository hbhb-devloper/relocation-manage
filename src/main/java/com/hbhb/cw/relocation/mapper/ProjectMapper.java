package com.hbhb.cw.relocation.mapper;

import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.web.vo.*;
import com.hbhb.web.beetlsql.BaseMapper;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.SqlResource;

import java.util.List;

/**
 * @author dxk
 * @since 2020-09-22
 */
@SqlResource("relocationProject")
public interface ProjectMapper extends BaseMapper<RelocationProject> {

 List<ProjectResVO> selectProjectByCondList(ProjectReqVO cond);

 PageResult<ProjectResVO> selectProjectByCond(ProjectReqVO cond, PageRequest<ProjectResVO> request);

 List<String> selectProjectNum();

 List<String> selectContractNumList();

 PageResult<StatementResVO> selectProjectStatementByUnitId(Integer unitId, PageRequest<ProjectResVO> request);

 List<AmountVO> selectCompensationAmount(List<String> list);

    List<ProjectSelectVO> selectSumConstructionBudget(List<String> list);

    List<RelocationProject> selectProject();

    List<ProjectSelectVO> selectSumCompensationAmount();

    List<String> selectProjectNumByProjectNum(List<String> list);

 List<WarnResVO> selectProjectFinalWarn();

 List<StatementResVO> selectProjectStatementListByUnitId(Integer unitId);

 List<WarnCountVO> selectProjectWarnCount();

 ProjectResVO selectProjectById(Long id);

 List<Long> selectProjectIdByContractNum(String contractNum);

 List<WarnResVO> selectProjectStartWarn();

}