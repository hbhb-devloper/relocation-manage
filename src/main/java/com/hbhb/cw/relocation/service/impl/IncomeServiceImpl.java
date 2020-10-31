package com.hbhb.cw.relocation.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.rpc.SysUserApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.SysUserInfo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private UnitApiExp unitService;

    @Resource
    private SysUserApiExp sysUserApiExp;


    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(".xlsx".equals(name) || ".xls".equals(name))) {
            throw new InvoiceException(InvoiceErrorCode.FILE_NAME_ERROR);
        }
    }

    /**
     * 收款分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param cond
     * @param userId
     * @return
     */
    @Override
    public PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize,
        IncomeReqVO cond, Integer userId) {
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
            BigDecimal monthAmount = relocationIncomeMapper
                .getMonthAmount(relocationIncomeResVO.getId(), DateUtil.getCurrentMonth());
            relocationIncomeResVO.setMonthAmount(monthAmount);
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
    public void addIncomeDetail(RelocationIncomeDetail detail,
        Integer userId) {
        String currentMonth = DateUtil.getCurrentMonth();
        detail.setPayMonth(currentMonth);
        detail.setPayMonth(detail.getPayMonth().replace("-", ""));
        SysUserInfo user = sysUserApiExp.getUserById(userId);
        String nickName = user.getNickName();
        detail.setPayee(nickName);
        detail.setCreateTime(DateUtil.getCurrentDate());
        incomeDetailMapper.insert(detail);
        //未收减少
        relocationIncomeMapper.updateIncomeUnreceived(detail.getIncomeId(), detail.getAmount());
        //已收增加
        relocationIncomeMapper.updateIncomeReceived(detail.getIncomeId(), detail.getAmount());
        RelocationIncome relocationIncome = relocationIncomeMapper.single(detail.getIncomeId());
        BigDecimal receivable = relocationIncome.getReceivable();
        BigDecimal unreceived = relocationIncome.getUnreceived();
        relocationIncome.setReceived(receivable.subtract(unreceived));
        //已收完的情况 3
        if (relocationIncome.getReceived().compareTo(relocationIncome.getReceivable()) == 0
            && relocationIncome.getUnreceived().compareTo(new BigDecimal("0")) == 0) {
            relocationIncomeMapper.updateIsReceived(detail.getIncomeId(), 3);
        }
        //未收完 2
        if (relocationIncome.getReceived().compareTo(relocationIncome.getReceivable()) < 0
            && relocationIncome.getUnreceived().compareTo(new BigDecimal("0")) > 0) {
            relocationIncomeMapper.updateIsReceived(detail.getIncomeId(), 2);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<IncomeImportVO> dataList) {
        // 转换单位
        List<Unit> list = unitService.getAllUnitList();
        Map<String, Integer> unitMap = list.stream().collect(
            Collectors.toMap(Unit::getShortName, Unit::getId));
        List<RelocationIncome> incomes = new ArrayList<>();
        for (IncomeImportVO importVO : dataList) {
            System.out.println(importVO);
            RelocationIncome relocationIncome = new RelocationIncome();
            // 1 迁改 2 搬迁 3 代建
            relocationIncome.setCategory("迁改".equals(importVO.getCategory()) ? 1
                : "搬迁".equals(importVO.getCategory()) ? 2 : 3);
            relocationIncome.setUnitId(
                unitMap.get(importVO.getUnit()) == null ? 11 : unitMap.get(importVO.getUnit()));
            relocationIncome.setSupplier(importVO.getSupplier());
            relocationIncome.setContractNum(importVO.getContractNum());
            relocationIncome.setContractName(importVO.getContractName());
            relocationIncome.setStartTime(DateUtil.string3DateYMD(importVO.getStartTime()));
            relocationIncome
                .setContractDeadline(DateUtil.string3DateYMD(importVO.getContractDeadline()));
            relocationIncome.setContractAmount(stringToBigDecimal(importVO.getContractAmount()));
            relocationIncome.setInvoiceTime(DateUtil.string3DateYMD(importVO.getInvoiceTime()));
            relocationIncome.setInvoiceNum(importVO.getInvoiceNum());
            relocationIncome.setInvoiceType(importVO.getInvoiceType());
            relocationIncome.setAmount(stringToBigDecimal(importVO.getAmount()));
            relocationIncome.setTax(stringToBigDecimal(importVO.getTax()));
            relocationIncome
                .setTaxIncludeAmount(stringToBigDecimal(importVO.getTaxIncludeAmount()));
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
    public List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo,
        Integer userId) {
        setConditionDetail(vo, userId);
        List<IncomeExportVO> relocationIncomeExportVOS = relocationIncomeMapper
            .selectExportList(vo);
        for (int i = 0; i < relocationIncomeExportVOS.size(); i++) {
            IncomeExportVO relocationIncomeExportVO = relocationIncomeExportVOS.get(i);
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
            }
            relocationIncomeExportVO.setNum(i + 1);
        }

        return relocationIncomeExportVOS;
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

    /*
     * 判断登录用户 添加时间查询 时分秒详情
     *
     * @param cond
     * @param loginUser
     */
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
