package com.hbhb.cw.relocation.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationProject;
import com.hbhb.cw.relocation.rpc.SysDictApiExp;
import com.hbhb.cw.relocation.service.ProjectService;
import com.hbhb.cw.relocation.web.vo.AmountVO;
import com.hbhb.cw.relocation.web.vo.ProjectImportVO;
import com.hbhb.cw.relocation.web.vo.ProjectReqVO;
import com.hbhb.cw.relocation.web.vo.ProjectResVO;
import com.hbhb.cw.systemcenter.api.UnitApi;
import com.hbhb.cw.systemcenter.enums.AllName;
import com.hbhb.cw.systemcenter.model.SysUser;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.ParentVO;
import com.hbhb.cw.systemcenter.vo.SysDictVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UnitApi unitApi;
    @Resource
    private SysDictApiExp sysDictApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationProject(List<ProjectImportVO> importVOList) {
        // 查询所有项目编号，用于做导入对比
        List<String> projectNumList = projectMapper.selectProjectNum();
        // 转换单位
        List<Unit> list = unitApi.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(Collectors.toMap(Unit::getUnitName, Unit::getId));
        // 主动迁改被动迁改（0-主动、1-被动）
        Map<String, Boolean> isInitiativeMap = new HashMap<>();
        isInitiativeMap.put("主动", true);
        isInitiativeMap.put("被动", false);
        // 有无补偿（0-无补偿、1-有补偿、2-项目取消）
        Map<String, Boolean> hasCompensationMap = new HashMap<>();
        hasCompensationMap.put("无", false);
        hasCompensationMap.put("有", true);
        // 所有合同编号
        List<String> contractNumList = new ArrayList<>();
        List<SysDictVO> stateList = sysDictApi.getCompensationSate();
        Map<String, String> stateMap = stateList.stream().collect(
                Collectors.toMap(SysDictVO::getLabel, SysDictVO::getValue));
        // 异常信息
        List<String> msg = new ArrayList<>();
        List<RelocationProject> projectList = new ArrayList<>();
        for (ProjectImportVO importVO : importVOList) {
            //获取合同编号
            contractNumList.add(importVO.getContractNum());
            RelocationProject project = new RelocationProject();
            BeanUtils.copyProperties(importVO, project);
            if (projectNumList.contains(importVO.getProjectNum())) {
                msg.add("项目编号为:" + importVO.getProjectNum() + "已存在");
            }
            project.setUnitId(unitMap.get(importVO.getUnitName()));
            project.setHasCompensation(hasCompensationMap.get(importVO.getHasCompensation()));
            project.setIsInitiative(isInitiativeMap.get(importVO.getIsInitiative()));
            project.setPlanStartTime(DateUtil.string3DateYMD(importVO.getPlanStartTime()));
            project.setPlanEndTime(DateUtil.string3DateYMD(importVO.getPlanEndTime()));
            project.setActualEndTime(DateUtil.string3DateYMD(importVO.getActualEndTime()));
            if (!isEmpty(importVO.getCompensationSate())) {
                project.setCompensationSate(Integer.valueOf(stateMap.get(importVO.getCompensationSate())));
            } else {
                project.setCompensationSate(0);
            }
            project.setContractNum(importVO.getContractNum() == null ? "" : importVO.getContractNum());
            project.setContractName(importVO.getContractName() == null ? "" : importVO.getContractName());
            project.setContractType(importVO.getContractType() == null ? "" : importVO.getContractType());
            project.setAnticipatePayable(importVO.getAnticipatePayable() == null ? new BigDecimal(0) : new BigDecimal(importVO.getAnticipatePayable()));
            project.setAnticipatePayment(importVO.getAnticipatePayment() == null ? new BigDecimal(0) : new BigDecimal(importVO.getAnticipatePayment()));
            project.setCompensationAmount(importVO.getCompensationAmount() == null ? new BigDecimal(0) : new BigDecimal(importVO.getCompensationAmount()));
            project.setConstructionAuditCost(importVO.getConstructionAuditCost() == null ? new BigDecimal(0) : new BigDecimal(importVO.getConstructionAuditCost()));
            project.setConstructionBudget(importVO.getConstructionBudget() == null ? new BigDecimal(0) : new BigDecimal(importVO.getConstructionBudget()));
            project.setConstructionCost(importVO.getConstructionCost() == null ? new BigDecimal(0) : new BigDecimal(importVO.getConstructionCost()));
            project.setFinalPayment(importVO.getFinalPayment() == null ? new BigDecimal(0) : new BigDecimal(importVO.getFinalPayment()));
            project.setMaterialBudget(importVO.getMaterialBudget() == null ? new BigDecimal(0) : new BigDecimal(importVO.getMaterialBudget()));
            project.setMaterialCost(importVO.getMaterialCost() == null ? new BigDecimal(0) : new BigDecimal(importVO.getMaterialCost()));
            // 判断合同状态
            try {
                if (!isEmpty(importVO.getCompensationSate())) {
                    int compensationSate = Integer.parseInt(stateMap.get(importVO.getCompensationSate()));
                    if (!isEmpty(importVO.getActualEndTime()) && compensationSate != 10 && compensationSate != 80) {
                        project.setContractDuration(DateUtil.monthBetween(importVO.getActualEndTime(), DateUtil.dateToStringYmd(new Date())));
                    } else {
                        project.setContractDuration(0);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            projectList.add(project);
        }
        // 检验导入数据是否有重复
        long count = importVOList.stream().distinct().count();
        if (count < importVOList.size()) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_INVOICE_EXIST_PROJECT_ERROR, msg.toString());
        } else if (!msg.isEmpty()) {
            throw new RelocationException(RelocationErrorCode.RELOCATION_IMPORT_DATE_ERROR, msg.toString());
        }
        // 更新赔补金额（新增的项目的施工费占整个合同的百分比）
        // 未导入前获取赔补赔补总额
        Map<String, BigDecimal> compensationMap = projectMapper.selectSumCompensationAmount();
        // 导入前合同编号
        HashSet<String> set = new HashSet<>(contractNumList);
        contractNumList.clear();
        contractNumList.addAll(set);
        projectMapper.insertBatch(projectList);
        // 导入后本次导入所有合同信息
        // 对比导入前后合同是否已存在 ，若未存在则不修改，若已存则修改
        List<String> contractNumNewList = new ArrayList<>();
        // 查出已存在的合同编号
        // todo 待优化
        if (!isEmpty(contractNumList) && !isEmpty(compensationMap)) {
            for (String contractNum : contractNumList) {
                for (String oldContractNum : compensationMap.keySet()) {
                    if (oldContractNum.equals(contractNum)) {
                        contractNumNewList.add(contractNum);
                    }
                }
            }
            if (contractNumNewList.size() != 0) {
                // 插入后赔补信息
                List<AmountVO> amountVO = projectMapper.selectCompensationAmount(contractNumNewList);
                // 需要修改合同编号施工费统计列表
                Map<String, BigDecimal> constructionBudgetMap = projectMapper.selectSumConstructionBudget(contractNumNewList);
                List<AmountVO> relocation = new ArrayList<>();
                for (AmountVO amount : amountVO) {
                    AmountVO relocationAmountVO = new AmountVO();
                    BigDecimal budgetTotal = constructionBudgetMap.get(amount.getContractNum());
                    // 修改项目
                    relocationAmountVO.setCompensationAmount((amount.getConstructionBudget().divide(budgetTotal, 2))
                            .multiply(compensationMap.get(amount.getContractNum())));
                    relocationAmountVO.setContractNum(amount.getContractNum());
                    relocationAmountVO.setConstructionBudget(amount.getConstructionBudget());
                    relocationAmountVO.setId(amount.getId());
                    relocation.add(relocationAmountVO);
                }
                // 按合同编号批量修改
                projectMapper.updateBatch(relocation);
            }
        }
    }


    @Override
    public List<String> getContractNumList() {
        return projectMapper.selectContractNumList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContractDuration() {
        // 查出所有除全额回款、合同未签订项目
        List<RelocationProject> projectList = projectMapper.selectProject();
        for (RelocationProject project : projectList) {
            project.setContractDuration(project.getContractDuration() + 1);
        }
        if (!projectList.isEmpty()) {
            // 批量修改未回款合同历时
            projectMapper.updateBatchTempById(projectList);
        }
    }


    @Override
    public PageResult<ProjectResVO> getRelocationProjectList(ProjectReqVO cond, Integer pageNum, Integer pageSize) {
        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        ParentVO parentUnit = unitApi.getParentUnit();
        if (parentUnit.getHangzhou().equals(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        PageResult<ProjectResVO> list = projectMapper.selectProjectByCond(cond, request);
        // 组装赔补状态
        List<Unit> unitList = unitApi.getAllUnitList();
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        Map<String, String> compensationSateMap = getCompensationSate();
        list.getList().forEach(item -> {
            item.setCompensationSate((compensationSateMap.get(item.getCompensationSate())));
            item.setUnitName((unitMap.get(item.getUnitId())));
                }
        );
        return list;
    }

    @Override
    public void updateRelocationProject(ProjectResVO projectResVO, SysUser user) {
        RelocationProject project = new RelocationProject();
        BeanUtils.copyProperties(projectResVO, project);
        //  计划完成时间
        project.setPlanEndTime(DateUtil.string2DateYMD(projectResVO.getPlanEndTime()));
        //计划实施时间
        project.setPlanStartTime(DateUtil.string2DateYMD(projectResVO.getPlanStartTime()));
        // 实际结束时间
        project.setActualEndTime(DateUtil.string2DateYMD(projectResVO.getActualEndTime()));
        //施工费（预算：元）
        project.setConstructionBudget(new BigDecimal(projectResVO.getConstructionBudget()));
        //甲供材料费(预算:元)
        project.setMaterialBudget(new BigDecimal(projectResVO.getMaterialBudget()));
        //施工费(送审结算:元)
        project.setConstructionCost(new BigDecimal(projectResVO.getConstructionCost()));
        //甲供材料费(送审结算:元)
        project.setMaterialCost(new BigDecimal(projectResVO.getMaterialCost()));
        //施工费审定金额(审计后:元)
        project.setConstructionAuditCost(new BigDecimal(projectResVO.getConstructionAuditCost()));
        //预付款应付金额（元）
        project.setAnticipatePayable(new BigDecimal(projectResVO.getAnticipatePayable()));
        // 预付款到账金额（元）
        project.setAnticipatePayment(new BigDecimal(projectResVO.getAnticipatePayment()));
        // 决算款到账金额（元）
        project.setFinalPayment(new BigDecimal(projectResVO.getFinalPayment()));
        projectMapper.updateById(project);
    }

    @Override
    public void deleteRelocationProject(Long id, SysUser user) {
        projectMapper.deleteById(id);
    }

    private Map<String, String> getCompensationSate() {
        List<SysDictVO> compensationSateList = sysDictApi.getCompensationSate();
        return compensationSateList.stream().collect(Collectors.toMap(SysDictVO::getValue, SysDictVO::getLabel));
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(AllName.XLS.getValue().equals(name) || AllName.XLSX.getValue().equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }
}
