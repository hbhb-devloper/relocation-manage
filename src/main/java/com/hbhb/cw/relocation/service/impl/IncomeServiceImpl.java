package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.InvoiceErrorCode;
import com.hbhb.cw.relocation.enums.IsReceived;
import com.hbhb.cw.relocation.enums.RelocationErrorCode;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.mapper.InvoiceMapper;
import com.hbhb.cw.relocation.mapper.ProjectMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UnitTopVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author hyk
 * @since 2020-09-28
 */
@Service
@Slf4j
public class
IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeMapper incomeMapper;

    @Resource
    private IncomeDetailMapper incomeDetailMapper;

    @Resource
    private UnitApiExp unitApiExp;

    @Resource
    private UserApiExp userApi;

    @Resource
    private DictApiExp dictApi;

    @Resource
    private ProjectMapper relocationProjectMapper;

    @Resource
    private InvoiceMapper invoiceMapper;

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue()
                .equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }

    @Override
    public PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize,
                                                 IncomeReqVO cond, Integer userId) {
        UnitTopVO parentUnit = unitApiExp.getTopUnit();
        List<Integer> unitIds = new ArrayList<>();
        if (parentUnit.getBenbu().equals(cond.getUnitId())) {
            unitIds = unitApiExp.getSubUnit(parentUnit.getBenbu());
        }
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        cond.setUnitIds(unitIds);
        setConditionDetail(cond, userId);
        PageRequest<IncomeResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<IncomeResVO> incomeList = incomeMapper.getIncomeList(cond, request);
        List<IncomeResVO> list = incomeList.getList();
        Map<String, String> typeMap = getInvoiceType();
        for (IncomeResVO incomeResVO : list) {
            String category = incomeResVO.getCategory();
            if ("1".equals(category)) {
                incomeResVO.setCategory("迁改");
            } else if ("2".equals(category)) {
                incomeResVO.setCategory("搬迁");
            } else if ("3".equals(category)) {
                incomeResVO.setCategory("代建");
            }
            //  拼装收款发票类型
            incomeResVO.setInvoiceType(typeMap.get(incomeResVO.getInvoiceType()));
            BigDecimal monthAmount = incomeMapper.getMonthAmount(incomeResVO.getId(), DateUtil.getCurrentMonth());
            incomeResVO.setMonthAmount(monthAmount);
            incomeResVO.setUnit(unitMap.get(Integer.valueOf(incomeResVO.getUnit())));
            if ("1".equals(incomeResVO.getIsReceived())) {
                incomeResVO.setIsReceived(IsReceived.RECEIVED.value());
            } else {
                incomeResVO.setIsReceived(IsReceived.NOT_RECEIVED.value());
            }
        }
        return incomeList;
    }

    @Override
    public List<RelocationIncomeDetail> getIncomeDetail(Long id, Integer isNeed) {
        String currentMonth = DateUtil.getCurrentMonth();
        return incomeMapper.selectDetailById(id, isNeed, currentMonth);
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
        UserInfo user = userApi.getUserInfoById(userId);
        String nickName = user.getNickName();
        BigDecimal amount = detail.getAmount();
        Long incomeId = detail.getIncomeId();
        detail.setPayee(nickName);
        detail.setCreateTime(DateUtil.getCurrentDate());
        incomeDetailMapper.insert(detail);
        RelocationIncome relocationIncome = incomeMapper.single(incomeId);
        //未收减少
        RelocationIncome single = incomeMapper.single(incomeId);
        RelocationIncome income = new RelocationIncome();
        income.setId(incomeId);
        BigDecimal subtractRe = single.getUnreceived().subtract(amount);
        income.setUnreceived(subtractRe);
        if (subtractRe.compareTo(new BigDecimal("0.0")) < 0) {
            throw new InvoiceException(InvoiceErrorCode.RELOCATION_INCOME_AMOUNT_ERROR);
        }
        //已收增加
        income.setReceived(single.getReceived().add(amount));
        incomeMapper.updateTemplateById(income);
        // 判断更新后的收款状态
        RelocationIncome income1 = incomeMapper.single(incomeId);
        BigDecimal receivable = income1.getReceivable();
        BigDecimal unreceived = income1.getUnreceived();
        BigDecimal received = income1.getReceived();
        //已收完的情况 3
        if (received.compareTo(receivable) == 0 && unreceived.compareTo(new BigDecimal("0")) == 0) {
            income1.setIsReceived(3);
            incomeMapper.updateTemplateById(income1);
        }
        //部分回款 2
        if (received.compareTo(receivable) < 0
            && relocationIncome.getUnreceived().compareTo(new BigDecimal("0")) > 0) {
            income1.setIsReceived(2);
            incomeMapper.updateTemplateById(income1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveRelocationInvoice(List<IncomeImportVO> dataList) {
        // 转换单位
        Map<String, Integer> unitMap = unitApiExp.getUnitMapByName();
        List<RelocationIncome> incomes = new ArrayList<>();
        Map<String, String> invoiceTypeMap = getInvoiceType();
        for (IncomeImportVO importVO : dataList) {
            RelocationIncome income = new RelocationIncome();
            // 1 迁改 2 搬迁 3 代建
            income.setCategory("迁改".equals(importVO.getCategory()) ? 1 : "搬迁".equals(importVO.getCategory()) ? 2 : 3);
            income.setUnitId(unitMap.get(importVO.getUnit()) == null ? 11 : unitMap.get(importVO.getUnit()));
            income.setSupplier(importVO.getSupplier());
            income.setContractNum(importVO.getContractNum());
            income.setContractName(importVO.getContractName());
            income.setStartTime(DateUtil.string3DateYMD(importVO.getStartTime()));
            income.setContractDeadline(DateUtil.string3DateYMD(importVO.getContractDeadline()));
            income.setContractAmount(stringToBigDecimal(importVO.getContractAmount()));
            income.setInvoiceTime(DateUtil.string3DateYMD(importVO.getInvoiceTime()));
            income.setInvoiceNum(importVO.getInvoiceNum());
            income.setInvoiceType(Integer.valueOf(String.valueOf(invoiceTypeMap.get(importVO.getInvoiceType()) != null
                ? invoiceTypeMap.get(importVO.getInvoiceType()) : 0)));
            income.setInvoiceType(Integer.valueOf(invoiceTypeMap.get(importVO.getInvoiceType())));
            income.setAmount(stringToBigDecimal(importVO.getAmount()));
            income.setTax(stringToBigDecimal(importVO.getTax()));
            income.setTaxIncludeAmount(stringToBigDecimal(importVO.getTaxIncludeAmount()));
            income.setConstructionName(importVO.getConstructionName());
            // 1 预付款   2 决算款
            income.setPaymentType("预付款".equals(importVO.getPaymentType()) ? 1 : 2);
            // 10-已收款 20-未收款 30-部分回款
            income.setIsReceived(IsReceived.NOT_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.NOT_RECEIVED.key()
                    : IsReceived.PART_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.PART_RECEIVED.key() : IsReceived.RECEIVED.key());
            income.setAging(importVO.getAging());
            income.setReceivable(stringToBigDecimal(importVO.getReceivable()));
            income.setReceived(stringToBigDecimal(importVO.getReceived()));
            income.setUnreceived(stringToBigDecimal(importVO.getUnreceived()));
            income.setAging(importVO.getAging());
            income.setReceivable(stringToBigDecimal(importVO.getReceivable()));
            income.setReceived(stringToBigDecimal(importVO.getReceived()));
            income.setUnreceived(stringToBigDecimal(importVO.getUnreceived()));
            //插入收款信息
            incomeMapper.insert(income);
            //插入收款下的收款详情
            if (!isEmpty(importVO.getReceiptNum())) {
                RelocationIncomeDetail incomeDetail = new RelocationIncomeDetail();
                incomeDetail.setIncomeId(income.getId());
                incomeDetail.setCreateTime(DateUtil.getCurrentDate());
                incomeDetail.setPayee(importVO.getPayee());
                incomeDetail.setReceiptNum(importVO.getReceiptNum());
                incomeDetail.setAmount(stringToBigDecimal(importVO.getMonthAmount()));
                incomeDetail.setPayMonth(DateUtil.getCurrentMonth());
                incomeDetailMapper.insert(incomeDetail);
            }
        }
    }

    @Override
    public List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<IncomeExportVO> relocationIncomeExportVos = incomeMapper
                .selectExportList(vo);
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
                case "20":
                    relocationIncomeExportVO.setIsReceived(IsReceived.NOT_RECEIVED.value());
                    break;
                case "30":
                    relocationIncomeExportVO.setIsReceived(IsReceived.PART_RECEIVED.value());
                    break;
                case "10":
                    relocationIncomeExportVO.setIsReceived(IsReceived.RECEIVED.value());
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
        if (!isEmpty(str)) {
            str = str.replace("   ", "");
            str = str.trim();
        }
        if (isEmpty(str)) {
            str = "0";
        } else if (str.contains("，")) {
            str = str.replace("，", "");
        } else if (str.contains(",")) {
            str = str.replace(",", "");
        }
        return new BigDecimal(str);
    }


    private void setConditionDetail(IncomeReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId)) {
            cond.setUnitId(user.getUnitId());
        }
        if (!isEmpty(cond.getStartTimeFrom())) {
            cond.setStartTimeFrom(cond.getStartTimeFrom() + " 00:00:00");
        }
        if (!isEmpty(cond.getStartTimeTo())) {
            cond.setStartTimeTo(cond.getStartTimeTo() + " 23:59:59");
        }
    }

    private Map<String, String> getInvoiceType() {
        List<DictVO> compensationSateList = dictApi.getDict(TypeCode.RELOCATION.value(), DictCode.RELOCATION_INVOICE_TYPE.value());
        return compensationSateList.stream().collect(Collectors.toMap(DictVO::getLabel, DictVO::getValue));
    }
}
