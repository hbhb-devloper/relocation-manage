package com.hbhb.cw.relocation.service.impl;import com.alibaba.excel.support.ExcelTypeEnum;import com.hbhb.api.core.bean.FileVO;import com.hbhb.core.utils.DateUtil;import com.hbhb.cw.relocation.enums.RelocationErrorCode;import com.hbhb.cw.relocation.enums.State;import com.hbhb.cw.relocation.exception.RelocationException;import com.hbhb.cw.relocation.mapper.ProjectMapper;import com.hbhb.cw.relocation.model.RelocationProject;import com.hbhb.cw.relocation.rpc.DictApiExp;import com.hbhb.cw.relocation.rpc.FlowRoleUserApiExp;import com.hbhb.cw.relocation.rpc.UserApiExp;import com.hbhb.cw.relocation.service.ProjectService;import com.hbhb.cw.relocation.util.BigDecimalUtil;import com.hbhb.cw.relocation.web.vo.AmountVO;import com.hbhb.cw.relocation.web.vo.ProjectImportVO;import com.hbhb.cw.relocation.web.vo.ProjectReqVO;import com.hbhb.cw.relocation.web.vo.ProjectResVO;import com.hbhb.cw.relocation.web.vo.ProjectSelectVO;import com.hbhb.cw.systemcenter.api.UnitApi;import com.hbhb.cw.systemcenter.enums.DictCode;import com.hbhb.cw.systemcenter.enums.TypeCode;import com.hbhb.cw.systemcenter.enums.UnitEnum;import com.hbhb.cw.systemcenter.model.SysUser;import com.hbhb.cw.systemcenter.model.Unit;import com.hbhb.cw.systemcenter.vo.DictVO;import com.hbhb.cw.systemcenter.vo.UserInfo;import lombok.extern.slf4j.Slf4j;import org.beetl.sql.core.page.DefaultPageRequest;import org.beetl.sql.core.page.PageRequest;import org.beetl.sql.core.page.PageResult;import org.springframework.beans.BeanUtils;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.multipart.MultipartFile;import javax.annotation.Resource;import java.math.BigDecimal;import java.text.ParseException;import java.util.ArrayList;import java.util.Date;import java.util.HashMap;import java.util.HashSet;import java.util.List;import java.util.Map;import java.util.concurrent.CopyOnWriteArrayList;import java.util.stream.Collectors;import static com.alibaba.excel.util.StringUtils.isEmpty;/** * @author wangxiaogang */@Service@Slf4j@SuppressWarnings(value = {"unchecked"})public class ProjectServiceImpl implements ProjectService {    @Resource    private UnitApi unitApi;    @Resource    private UserApiExp userAip;    @Resource    private DictApiExp sysDictApi;    @Resource    private FlowRoleUserApiExp flowApi;    @Resource    private ProjectMapper projectMapper;    private final List<String> msg = new CopyOnWriteArrayList<>();    @Override    public PageResult<ProjectResVO> getRelocationProjectList(ProjectReqVO cond, Integer pageNum, Integer pageSize, Integer userId) {        // 判断入参条件单位        PageRequest<ProjectResVO> request = DefaultPageRequest.of(pageNum, pageSize);        if (UnitEnum.isHangzhou(cond.getUnitId())) {            cond.setUnitId(null);        }        // 判断用户单位        UserInfo user = userAip.getUserInfoById(userId);        Unit unitInfo = unitApi.getUnitInfo(user.getUnitId());        if (!UnitEnum.isHangzhou(user.getUnitId())) {            cond.setUnitId(user.getUnitId());        }        if ("网络部".equals(unitInfo.getUnitName())) {            cond.setUnitId(null);        }        PageResult<ProjectResVO> list = projectMapper.selectProjectByCond(cond, request);        // 单位名称        Map<Integer, String> unitMap = unitApi.getUnitMapById();        // 组装赔补状态        Map<String, String> compensationSateMap = getCompensationSate();        List<String> contractNum = projectMapper.selectContractNumList();        // 赔补总额        List<ProjectSelectVO> compensationAmount = projectMapper.selectSumConstructionBudget(contractNum);        Map<String, BigDecimal> totalCompensationAmount = compensationAmount.stream().collect(Collectors.                toMap(ProjectSelectVO::getNum, ProjectSelectVO::getCompensationAmount));        // 组装        list.getList().forEach(item -> {                    item.setCompensationSate((compensationSateMap.get(item.getCompensationSate())));                    item.setUnitName((unitMap.get(item.getUnitId())));                    item.setTotalCompensationAmount(!isEmpty(item.getContractNum())                            ? totalCompensationAmount.get(item.getContractNum()).toString() : "0");                    item.setIsFile(isEmpty(item.getFileId()) ? State.NO.value() : State.YES.value());                }        );        return list;    }    @Override    @Transactional(rollbackFor = Exception.class)    public synchronized void addSaveRelocationProject(List<ProjectImportVO> importVOList, Map<Integer, String> importHeadMap) throws ParseException {        // 对比导入表头与模板表头若相同则执行后续操作，若不同则抛出异常        Map<Integer, String> headMap = projectHead();        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {            String m1value = entry.getValue() == null ? "" : entry.getValue();            String m2value = importHeadMap.get(entry.getKey()) == null ? "" : importHeadMap.get(entry.getKey());            if (!m1value.equals(m2value)) {                //若两个map中相同key对应的value不相等                throw new RelocationException(RelocationErrorCode.RELOCATION_TEMPLATE_ERROR);            }        }        // 查询所有项目编号，用于做导入对比        List<String> projectNumList = projectMapper.selectProjectNum();        // 转换单位        Map<String, Integer> unitMap = unitApi.getUnitMapByUnitName();        // 主动迁改被动迁改（0-主动、1-被动）        Map<String, Boolean> isInitiativeMap = new HashMap<>(100);        isInitiativeMap.put("主动", true);        isInitiativeMap.put("被动", false);        // 有无补偿（0-无补偿、1-有补偿、2-项目取消）        Map<String, Boolean> hasCompensationMap = new HashMap<>(100);        hasCompensationMap.put("无", false);        hasCompensationMap.put("有", true);        // 所有合同编号        List<String> contractNumList = new ArrayList<>();        Map<String, String> stateMap = getCompensationSateLabel();        // 异常信息        List<String> error = new ArrayList<>();        List<RelocationProject> projectList = new ArrayList<>();        int i = 3;        for (ProjectImportVO importVO : importVOList) {            //获取合同编号            contractNumList.add(importVO.getContractNum());            RelocationProject project = new RelocationProject();            BeanUtils.copyProperties(importVO, project);            if (projectNumList.contains(importVO.getProjectNum())) {                error.add("在excel表中第" + i + "行，项目编号为:" + importVO.getProjectNum() + "已存在基础信息表中");            }            i++;            project.setUnitId(unitMap.get(importVO.getUnitName()));            project.setHasCompensation(hasCompensationMap.get(importVO.getHasCompensation()));            project.setIsInitiative(isInitiativeMap.get(importVO.getIsInitiative()));            project.setPlanStartTime(DateUtil.string3DateYMD(importVO.getPlanStartTime()));            project.setPlanEndTime(DateUtil.string3DateYMD(importVO.getPlanEndTime()));            project.setActualEndTime(DateUtil.string3DateYMD(importVO.getActualEndTime()));            if (!isEmpty(importVO.getCompensationSate())) {                project.setCompensationSate(Integer.valueOf(!isEmpty(stateMap.get(importVO.getCompensationSate()))                        ? stateMap.get(importVO.getCompensationSate()) : "0"));            } else {                project.setCompensationSate(0);            }            project.setContractNum(importVO.getContractNum() == null ? "" : importVO.getContractNum());            project.setContractName(importVO.getContractName() == null ? "" : importVO.getContractName());            project.setContractType(importVO.getContractType() == null ? "" : importVO.getContractType());            project.setAnticipatePayable(BigDecimalUtil.getBigDecimal(importVO.getAnticipatePayable()));            project.setAnticipatePayment(BigDecimalUtil.getBigDecimal(importVO.getAnticipatePayment()));            project.setCompensationAmount(BigDecimalUtil.getBigDecimal(importVO.getCompensationAmount()));            project.setConstructionAuditCost(BigDecimalUtil.getBigDecimal(importVO.getConstructionAuditCost()));            project.setConstructionBudget(BigDecimalUtil.getBigDecimal(importVO.getConstructionBudget()));            project.setConstructionCost(BigDecimalUtil.getBigDecimal(importVO.getConstructionCost()));            project.setFinalPayment(BigDecimalUtil.getBigDecimal(importVO.getFinalPayment()));            project.setMaterialBudget(BigDecimalUtil.getBigDecimal(importVO.getMaterialBudget()));            project.setMaterialCost(BigDecimalUtil.getBigDecimal(importVO.getMaterialCost()));            // 判断合同状态            if (!isEmpty(importVO.getCompensationSate())) {                int compensationSate = Integer.parseInt(!isEmpty(stateMap.get(importVO.getCompensationSate()))                        ? stateMap.get(importVO.getCompensationSate()) : "0");                if (!isEmpty(importVO.getActualEndTime()) && compensationSate != 10 && compensationSate != 80) {                    project.setContractDuration(DateUtil.monthBetween(importVO.getActualEndTime(), DateUtil.dateToStringYmd(new Date())));                } else {                    project.setContractDuration(0);                }            }            projectList.add(project);        }        // 检验导入数据是否有重复        msg.clear();        long count = importVOList.stream().distinct().count();        if (count < importVOList.size()) {            msg.add("本次导入共存在" + count + "条数据,请检查后从新导入");        }        msg.addAll(error);        //若不存在错误信息则新增项目并修改赔补金额        if (error.size() == 0) {            List<RelocationProject> projects = updateCompensationAmount(contractNumList, projectList);            projectMapper.insertBatch(projects);        }    }    @Override    public List<String> getContractNumList() {        return projectMapper.selectContractNumList();    }    @Override    @Transactional(rollbackFor = Exception.class)    public void updateContractDuration() {        // 查出所有除全额回款、合同未签订项目        List<RelocationProject> projectList = projectMapper.selectProject();        for (RelocationProject project : projectList) {            project.setContractDuration(project.getContractDuration() == null ? 1 : project.getContractDuration() + 1);        }        if (!projectList.isEmpty()) {            // 批量修改未回款合同历时            projectMapper.updateBatchTempById(projectList);        }    }    @Override    public void updateRelocationProject(ProjectResVO projectResVO, SysUser user) {        RelocationProject project = new RelocationProject();        BeanUtils.copyProperties(projectResVO, project);        //  计划完成时间        project.setCompensationSate(Integer.valueOf(projectResVO.getCompensationSate()));        project.setPlanEndTime(DateUtil.string2DateYMD(projectResVO.getPlanEndTime()));        //计划实施时间        project.setPlanStartTime(DateUtil.string2DateYMD(projectResVO.getPlanStartTime()));        // 实际结束时间        project.setActualEndTime(DateUtil.string2DateYMD(projectResVO.getActualEndTime()));        //施工费（预算：元）        project.setConstructionBudget(new BigDecimal(projectResVO.getConstructionBudget()));        //甲供材料费(预算:元)        project.setMaterialBudget(new BigDecimal(projectResVO.getMaterialBudget()));        //施工费(送审结算:元)        project.setConstructionCost(new BigDecimal(projectResVO.getConstructionCost()));        //甲供材料费(送审结算:元)        project.setMaterialCost(new BigDecimal(projectResVO.getMaterialCost()));        //施工费审定金额(审计后:元)        project.setConstructionAuditCost(new BigDecimal(projectResVO.getConstructionAuditCost()));        //预付款应付金额（元）        project.setAnticipatePayable(new BigDecimal(projectResVO.getAnticipatePayable()));        // 预付款到账金额（元）        project.setAnticipatePayment(new BigDecimal(projectResVO.getAnticipatePayment()));        // 决算款到账金额（元）        project.setFinalPayment(new BigDecimal(projectResVO.getFinalPayment()));        // 赔补金额        project.setCompensationAmount(new BigDecimal(projectResVO.getCompensationAmount()));        projectMapper.updateById(project);    }    @Override    public void deleteRelocationProject(Long id, Integer userId) {        // 获取        List<Integer> userIdList = flowApi.getUserIdByRoleName("迁改基础信息删除人");        if (!userIdList.contains(userId)) {            throw new RelocationException(RelocationErrorCode.RELOCATION_PROJECT_PERMISSION_DENIED);        }        projectMapper.deleteById(id);    }    @Override    public void deleteBatch(List<Long> ids) {        //   todo  批量删除 目前项目信息与发票收据未做关联，不去验证是否有相关联的数据        // 1.查询基础表中已关联的发票、收据、以及预警中不可进行删除的id        // 2.批量删除无关联的项目    }    @Override    public ProjectResVO getProject(Long id) {        return projectMapper.selectProjectById(id);    }    @Override    public Boolean judgeContractNum(MultipartFile file) {        // 获取所有上传文件名        String fileName = file.getOriginalFilename();        if (fileName != null) {            String fileNames = fileName.substring(0, fileName.indexOf("."));            // 获取所有合同编号            List<String> contractNum = projectMapper.selectContractNumList();            // 比较判断上传文件与合同编号对应，对应则返回true,否则为false            return contractNum.contains(fileNames);        }        return null;    }    @Override    @Transactional(rollbackFor = Exception.class)    public void updateContractFileId(FileVO file) {        // 文件名称与合同编号一致        String fileName = file.getFileName();        // 获取合同编号        String contractNum = fileName.substring(0, fileName.indexOf("."));        if (contractNum.contains("(")) {            contractNum = contractNum.substring(0, contractNum.indexOf("("));        }        Long fileId = (file.getId());        // 获取该合同下所有项目id        List<RelocationProject> list = new ArrayList<>();        List<Long> projectIds = projectMapper.selectProjectIdByContractNum(contractNum);        if (projectIds != null) {            for (Long projectId : projectIds) {                RelocationProject project = new RelocationProject();                project.setId(projectId);                project.setFileId(fileId);                list.add(project);            }        }        //批量修改合同文件id        projectMapper.updateBatchTempById(list);    }    @Override    public void judgeFileName(String fileName) {        int i = fileName.lastIndexOf(".");        String name = fileName.substring(i);        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue().equals(name))) {            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);        }    }    @Override    public List<String> getMsg() {        return this.msg;    }    private List<RelocationProject> updateCompensationAmount(List<String> contractNumList, List<RelocationProject> projectList) {        // 更新赔补金额（新增的项目的施工费占整个合同的百分比）        // 获取导入前所有合同编号        List<String> oldContractNumList = projectMapper.selectContractNumList();        // 2.获取本次导入合同编号        HashSet<String> set = new HashSet<>(contractNumList);        contractNumList.clear();        contractNumList.addAll(set);        // 3.对比导入前后合同是否已存在 ，若不存在则不修改，若已存则修改        List<String> contractNumNewList = new ArrayList<>();        if (!isEmpty(contractNumList) && !isEmpty(oldContractNumList)) {            for (String contractNum : contractNumList) {                for (String oldContractNum : oldContractNumList) {                    if (oldContractNum.equals(contractNum)) {                        contractNumNewList.add(contractNum);                    }                }            }        }        // 4.判断本次导入是否有与已存在的合同编号        if (!isEmpty(contractNumNewList)) {            // 4.1 若存在则获取本次导入前所有存在同一合同项目的施工费，预付款应付金额，赔补金额            List<AmountVO> amountVO = projectMapper.selectCompensationAmount(contractNumNewList);            // 4.1.1统计已存在项目施工费总额            List<ProjectSelectVO> totalList = projectMapper.selectSumConstructionBudget(contractNumNewList);            Map<String, BigDecimal> constructionBudgetMap = totalList.stream().collect(Collectors.                    toMap(ProjectSelectVO::getNum, ProjectSelectVO::getConstructionBudget));            // 4.1.2 统计已存在项目预付款应付总额            Map<String, BigDecimal> anticipatePayableMap = totalList.stream().collect(Collectors.                    toMap(ProjectSelectVO::getNum, ProjectSelectVO::getAnticipatePayable));            Map<String, BigDecimal> compensationMap = totalList.stream()                    .collect(Collectors.toMap(ProjectSelectVO::getNum, ProjectSelectVO::getCompensationAmount));            // 4.2 修改本次导入同一合同下赔补金额以及已存在的赔补金额按（施工费占整个合同的百分比）            List<RelocationProject> newProjectList = new ArrayList<>();            // 4.3 修改已存在表中的项目赔补金额            List<RelocationProject> oldProjectList = new ArrayList<>();            // 统计本次导入与已有相同项目合同编号（导入施工费） 合计            Map<String, BigDecimal> total = new HashMap<>(2000);            for (String contract : contractNumList) {                BigDecimal budgetTotal = BigDecimal.ZERO;                for (RelocationProject relocation : projectList) {                    if (contractNumList.contains(relocation.getContractNum())) {                        if (relocation.getContractNum().equals(contract)) {                            // 累加预算施工费                            budgetTotal = budgetTotal.add(relocation.getConstructionBudget());                        }                    }                }                total.put(contract, budgetTotal);            }            for (RelocationProject relocation : projectList) {                RelocationProject newProject = new RelocationProject();                BeanUtils.copyProperties(relocation, newProject);                if (contractNumList.contains(relocation.getContractNum())) {                    // 本次导入施工费                    String contractNum = relocation.getContractNum();                    // 计算本次导入赔补金额                    newProject.setCompensationAmount(((relocation.getConstructionBudget())                            .divide((constructionBudgetMap.get(contractNum)                                    .add(total.get(contractNum))), 4, 4))                            .multiply(compensationMap.get(contractNum)));                    // 计算本次导入预付款应付金额                    newProject.setAnticipatePayable(((relocation.getConstructionBudget())                            .divide((constructionBudgetMap.get(contractNum)                                    .add(total.get(contractNum))), 4, 4))                            .multiply(anticipatePayableMap.get(contractNum)));                    // 4.3.1计算已存在项目赔补金额                    for (AmountVO amount : amountVO) {                        if (amount.getContractNum().equals(relocation.getContractNum())) {                            RelocationProject oldProject = new RelocationProject();                            // 施工费                            BigDecimal oldTotal = amount.getConstructionBudget();                            // 计算已存在赔补金额                            oldProject.setCompensationAmount(oldTotal.divide((constructionBudgetMap.get(contractNum)                                    .add(total.get(contractNum))), 4, 4)                                    .multiply(compensationMap.get(contractNum)));                            // 计算已存在预付款应付金额                            oldProject.setAnticipatePayable(oldTotal.divide((constructionBudgetMap.get(contractNum)                                    .add(total.get(contractNum))), 4, 4)                                    .multiply(anticipatePayableMap.get(contractNum)));                            oldProject.setId(amount.getId());                            oldProjectList.add(oldProject);                        }                    }                }                // 修改已存在赔补金额                projectMapper.updateBatchTempById(oldProjectList);                //4.2.1 返回本次你导入数据                newProjectList.add(newProject);            }            return newProjectList;        }        return projectList;    }    private Map<String, String> getCompensationSate() {        // 获取赔补状态        List<DictVO> compensationSateList = sysDictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_PROJECT_COMPENSATION.value());        return compensationSateList.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));    }    private Map<String, String> getCompensationSateLabel() {        // 获取赔补状态        List<DictVO> compensationSateList = sysDictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_PROJECT_COMPENSATION.value());        return compensationSateList.stream().collect(Collectors.toMap(DictVO::getLabel, DictVO::getValue));    }    private Map<Integer, String> projectHead() {        Map<Integer, String> headMap = new HashMap<>(100);        headMap.put(0, "区域");        headMap.put(1, "迁改项目编号");        headMap.put(2, "EOMS迁移修缮管理流程工单号");        headMap.put(3, "EOMS光缆割接流程工单号");        headMap.put(4, "计划施工时间");        headMap.put(5, "计划完成时间");        headMap.put(6, "实际完工时间");        headMap.put(7, "施工单位");        headMap.put(8, "工程名称");        headMap.put(9, "迁改涉及网络层级（省干、汇聚、接入、驻地网）");        headMap.put(10, "施工费(预算:元)");        headMap.put(11, "甲供材料费(预算:元)");        headMap.put(12, "施工费(送审结算:元)");        headMap.put(13, "甲供材料费(送审结算:元)");        headMap.put(14, "施工费审定金额(审计后:元)");        headMap.put(15, "主动迁改或者被动");        headMap.put(16, "性质归类");        headMap.put(17, "迁改原因");        headMap.put(18, "对方单位");        headMap.put(19, "对方联系人");        headMap.put(20, "对方联系电话");        headMap.put(21, "有无赔补");        headMap.put(22, "被动无赔类型");        headMap.put(23, "合同编号");        headMap.put(24, "赔补合同名");        headMap.put(25, "赔补金额（元）");        headMap.put(26, "预付款应付金额（元）");        headMap.put(27, "预付款到账金额（元）");        headMap.put(28, "决算款到账金额（元）\n" +                "（注：决算款不包含预付款）");        headMap.put(29, "赔补状态（合同签订中/预付款未开票/\n" +                "预付款已开票未到账/\n" +                "预付款已到账，施工中/\n" +                "决算编制审计中/\n" +                "决算款已开票未到账/\n" +                "全额回款）注：必须从以上选项中");        headMap.put(30, "未全额回款合同\n" +                "合同签订时长（月）");        headMap.put(31, "赔补特殊情况备注（赔补性质变更、决算款有调整或小于协议金额等特殊情况说明）");        headMap.put(32, "月报");        headMap.put(33, "年份");        headMap.put(34, "合同类型");        return headMap;    }}