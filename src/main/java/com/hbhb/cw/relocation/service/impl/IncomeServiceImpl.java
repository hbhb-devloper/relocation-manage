package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.systemcenter.enums.AllName;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.ParentVO;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyk
 * @since 2020-09-28
 */
@Service
@Slf4j
public class
IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeMapper relocationIncomeMapper;

    @Resource
    private IncomeDetailMapper incomeDetailMapper;

    @Resource
    private UnitApiExp unitApiExp;

    @Resource
    private SysUserApiExp sysUserApiExp;

    @Resource
    private ProjectMapper relocationProjectMapper;

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(AllName.XLS.getValue().equals(name) || AllName.XLSX.getValue().equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }

    @Override
    public PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize, IncomeReqVO cond, Integer userId) {
        List<Unit> unitList = unitApiExp.getAllUnitList();
        ParentVO parentUnit = unitApiExp.getParentUnit();
        List<Integer> unitIds = new ArrayList<>();
        for (Unit unit : unitList) {
            if (parentUnit.getBenbu().equals(cond.getUnitId())) {
                unitIds.add(unit.getId());
            }
        }
        Map<Integer, String> unitMap = unitList.stream().collect(Collectors.toMap(Unit::getId, Unit::getUnitName));
        cond.setUnitIds(unitIds);
        setConditionDetail(cond, userId);
        PageRequest<IncomeResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<IncomeResVO> incomeList = relocationIncomeMapper.getIncomeList(cond, request);
        List<IncomeResVO> list = incomeList.getList();
        for (IncomeResVO relocationIncomeResVO : list) {
            String category = relocationIncomeResVO.getCategory();
            if ("1".equals(category)) {
                relocationIncomeResVO.setCategory("迁改");
            } else if ("2".equals(category)) {
                relocationIncomeResVO.setCategory("搬迁");
            } else if ("3".equals(category)) {
                relocationIncomeResVO.setCategory("代建");
            }
            BigDecimal monthAmount = relocationIncomeMapper.getMonthAmount(relocationIncomeResVO.getId(), DateUtil.getCurrentMonth());
            relocationIncomeResVO.setMonthAmount(monthAmount);
            relocationIncomeResVO.setUnit(unitMap.get(Integer.valueOf(relocationIncomeResVO.getUnit())));
            if ("1".equals(relocationIncomeResVO.getIsReceived())) {
                relocationIncomeResVO.setIsReceived(IsReceived.RECEIVED.value());
            } else {
                relocationIncomeResVO.setIsReceived(IsReceived.NOT_RECEIVED.value());
            }
        }
        return incomeList;
    }

    @Override
    public List<RelocationIncomeDetail> getIncomeDetail(Long id, Integer isNeed) {
        String currentMonth = DateUtil.getCurrentMonth();
        return relocationIncomeMapper.selectDetailById(id, isNeed, currentMonth);
    }

    @Override
    public void updateIncomeDetail(RelocationIncomeDetail detail) {
        incomeDetailMapper.updateById(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addIncomeDetail(RelocationIncomeDetail detail, Integer userId) {
        String currentMonth = DateUtil.getCurrentMonth();
        detail.setPayMonth(currentMonth);
        detail.setPayMonth(detail.getPayMonth().replace("-", ""));
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        String nickName = user.getNickName();
        BigDecimal amount = detail.getAmount();
        Long incomeId = detail.getIncomeId();
        detail.setPayee(nickName);
        detail.setCreateTime(DateUtil.getCurrentDate());
        incomeDetailMapper.insert(detail);
        RelocationIncome relocationIncome = relocationIncomeMapper.single(incomeId);

        // todo 此处应该使用备注格式字段去反查，而不是发票编号
        Long pid = relocationIncomeMapper.selectProject(relocationIncome.getInvoiceNum());
        Integer paymentType = relocationIncome.getPaymentType();
//        if (pid == null) {
//            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_NOT_PROJECT);
//        } else {
//            RelocationProject project = relocationProjectMapper.single(pid);
//            BigDecimal anticipatePayment = project.getAnticipatePayment();
//            BigDecimal finalPayment = project.getFinalPayment();
//            RelocationProject relocationProject = new RelocationProject();
//            // 同步修改项目信息表数据
//            if (paymentType == 1) {
//                relocationProject.setAnticipatePayment(anticipatePayment.add(amount));
//            } else {
//                relocationProject.setFinalPayment(finalPayment.add(amount));
//            }
//            relocationProjectMapper.updateTemplateById(relocationProject);
//        }
        //未收减少
        RelocationIncome single = relocationIncomeMapper.single(incomeId);
        RelocationIncome income = new RelocationIncome();
        income.setId(incomeId);
        income.setUnreceived(single.getUnreceived().subtract(amount));
        income.setReceived(single.getReceived().add(amount));
        relocationIncomeMapper.updateTemplateById(income);
        //已收增加
        RelocationIncome single1 = relocationIncomeMapper.single(incomeId);
        BigDecimal receivable = single1.getReceivable();
        BigDecimal unreceived = single1.getUnreceived();
        relocationIncome.setReceived(receivable.subtract(unreceived));
        relocationIncomeMapper.updateTemplateById(single1);
        //已收完的情况 3
        RelocationIncome income1 = new RelocationIncome();
        income1.setId(incomeId);
        if (relocationIncome.getReceived().compareTo(relocationIncome.getReceivable()) == 0
                && relocationIncome.getUnreceived().compareTo(new BigDecimal("0")) == 0) {
            income1.setIsReceived(3);
            relocationIncomeMapper.updateTemplateById(income1);
        }
        //未收完 2
        if (relocationIncome.getReceived().compareTo(relocationIncome.getReceivable()) < 0
                && relocationIncome.getUnreceived().compareTo(new BigDecimal("0")) > 0) {
            income1.setIsReceived(2);
            relocationIncomeMapper.updateTemplateById(income1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<IncomeImportVO> dataList) {
        // 转换单位
        List<Unit> list = unitApiExp.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(Collectors.toMap(Unit::getShortName, Unit::getId));
        List<RelocationIncome> incomes = new ArrayList<>();
        for (IncomeImportVO importVO : dataList) {
            RelocationIncome relocationIncome = new RelocationIncome();
            // 1 迁改 2 搬迁 3 代建
            relocationIncome.setCategory("迁改".equals(importVO.getCategory()) ? 1 : "搬迁".equals(importVO.getCategory()) ? 2 : 3);
            relocationIncome.setUnitId(unitMap.get(importVO.getUnit()) == null ? 11 : unitMap.get(importVO.getUnit()));
            relocationIncome.setSupplier(importVO.getSupplier());
            relocationIncome.setContractNum(importVO.getContractNum());
            relocationIncome.setContractName(importVO.getContractName());
            relocationIncome.setStartTime(DateUtil.string3DateYMD(importVO.getStartTime()));
            relocationIncome.setContractDeadline(DateUtil.string3DateYMD(importVO.getContractDeadline()));
            relocationIncome.setContractAmount(stringToBigDecimal(importVO.getContractAmount()));
            relocationIncome.setInvoiceTime(DateUtil.string3DateYMD(importVO.getInvoiceTime()));
            relocationIncome.setInvoiceNum(importVO.getInvoiceNum());
            relocationIncome.setInvoiceType(importVO.getInvoiceType());
            relocationIncome.setAmount(stringToBigDecimal(importVO.getAmount()));
            relocationIncome.setTax(stringToBigDecimal(importVO.getTax()));
            relocationIncome.setTaxIncludeAmount(stringToBigDecimal(importVO.getTaxIncludeAmount()));
            relocationIncome.setConstructionName(importVO.getConstructionName());
            // 1 预付款 2 尾款  3 决算款
            relocationIncome.setPaymentType(1);
            // 1 未收款 2 未收完  3 已收完
            relocationIncome.setIsReceived("未收款".equals(importVO.getCategory().trim()) ? 1 : 2);
            relocationIncome.setAging(importVO.getAging());
            relocationIncome.setReceivable(stringToBigDecimal(importVO.getReceivable()));
            relocationIncome.setReceived(stringToBigDecimal(importVO.getReceived()));
            relocationIncome.setUnreceived(stringToBigDecimal(importVO.getUnreceived()));
            incomes.add(relocationIncome);
        }
        relocationIncomeMapper.insertBatch(incomes);
    }

    @Override
    public List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<IncomeExportVO> relocationIncomeExportVos = relocationIncomeMapper.selectExportList(vo);
        for (int i = 0; i < relocationIncomeExportVos.size(); i++) {
            IncomeExportVO relocationIncomeExportVO = relocationIncomeExportVos.get(i);
            String category = relocationIncomeExportVO.getCategory();
            switch (category) {
                case "1":
                    relocationIncomeExportVO.setCategory("迁改");
                    break;
                case "2":
                    relocationIncomeExportVO.setCategory("搬迁");
                    break;
                case "3":
                    relocationIncomeExportVO.setCategory("代建");
                    break;
                default:
                    relocationIncomeExportVO.setCategory("");
                    break;
            }
            String isReceived = relocationIncomeExportVO.getIsReceived();
            switch (isReceived) {
                case "1":
                    relocationIncomeExportVO.setIsReceived("未收款");
                    break;
                case "2":
                    relocationIncomeExportVO.setIsReceived("收款中");
                    break;
                case "3":
                    relocationIncomeExportVO.setIsReceived("已收完");
                    break;
                default:
                    relocationIncomeExportVO.setIsReceived("");
                    break;
            }
            relocationIncomeExportVO.setNum(i + 1);
        }

        return relocationIncomeExportVos;
    }

    public BigDecimal stringToBigDecimal(String str) {
        if (!StringUtils.isEmpty(str)) {
            str = str.trim();
            str = str.replace(" ", "");
        }
        if (StringUtils.isEmpty(str)) {
            str = "0";
        } else if (str.contains("，")) {
            str = str.replace("，", "");
        } else if (str.contains(",")) {
            str = str.replace(",", "");
        }
        return new BigDecimal(str);
    }


    private void setConditionDetail(IncomeReqVO cond, Integer userId) {
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        if (!"admin".equals(user.getUserName())) {
            cond.setUnitId(user.getUnitId());
        }

        if (!StringUtils.isEmpty(cond.getStartTimeFrom())) {
            cond.setStartTimeFrom(cond.getStartTimeFrom() + " 00:00:00");
        }
        if (!StringUtils.isEmpty(cond.getStartTimeTo())) {
            cond.setStartTimeTo(cond.getStartTimeTo() + " 23:59:59");
        }
    }
}
