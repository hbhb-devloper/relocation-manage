package com.hbhb.cw.relocation.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.web.vo.AmountVO;
import com.hbhb.cw.relocation.web.vo.ContractInfoVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.relocation.web.vo.ProjectSelectVO;
import com.hbhb.cw.relocation.web.vo.StatementResVO;
import com.hbhb.cw.relocation.web.vo.WarnCountVO;
import com.hbhb.cw.relocation.web.vo.WarnResVO;
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

    List<ProjectReqVO> selectProjectByCondList(ProjectReqVO cond);

    PageResult<ProjectResVO> selectProjectByCond(ProjectReqVO cond, PageRequest<ProjectResVO> request);

    List<String> selectProjectNum();

    List<String> selectContractNumList();

    PageResult<StatementResVO> selectProjectStatementByUnitId(Integer unitId, PageRequest<ProjectResVO> request);

    List<AmountVO> selectCompensationAmount(List<String> list);

    List<ProjectSelectVO> selectSumConstructionBudget(List<String> list);

    List<RelocationProject> selectProject();


    List<String> selectProjectNumByProjectNum(List<String> list);

    List<StatementResVO> selectProjectStatementListByUnitId(Integer unitId);

    List<WarnResVO> selectProjectStartWarn();

    List<WarnCountVO> selectProjectStartWarnCount();

    List<WarnResVO> selectProjectFinalWarn();

    List<WarnCountVO> selectProjectFinalWarnCount();

    List<Long> selectProjectIdByContractNum(String contractNum);

    ProjectResVO selectProjectById(Long id);

    List<ContractInfoVO> selectContractInfo();
}