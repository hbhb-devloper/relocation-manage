package com.hbhb.cw.relocation.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.relocation.enums.*;
import com.hbhb.cw.relocation.exception.InvoiceException;
import com.hbhb.cw.relocation.exception.RelocationException;
import com.hbhb.cw.relocation.mapper.IncomeDetailMapper;
import com.hbhb.cw.relocation.mapper.IncomeMapper;
import com.hbhb.cw.relocation.model.RelocationIncome;
import com.hbhb.cw.relocation.model.RelocationIncomeDetail;
import com.hbhb.cw.relocation.rpc.DictApiExp;
import com.hbhb.cw.relocation.rpc.UnitApiExp;
import com.hbhb.cw.relocation.rpc.UserApiExp;
import com.hbhb.cw.relocation.service.IncomeService;
import com.hbhb.cw.relocation.util.BigDecimalUtil;
import com.hbhb.cw.relocation.web.vo.IncomeExportVO;
import com.hbhb.cw.relocation.web.vo.IncomeImportVO;
import com.hbhb.cw.relocation.web.vo.IncomeReqVO;
import com.hbhb.cw.relocation.web.vo.IncomeResVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
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

    @Override
    public PageResult<IncomeResVO> getIncomeList(Integer pageNum, Integer pageSize,
                                                 IncomeReqVO cond, Integer userId) {

        List<Integer> unitIds = new ArrayList<>();
        if (UnitEnum.isBenbu(cond.getUnitId())) {
            unitIds = unitApiExp.getSubUnit(cond.getUnitId());
        }
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        cond.setUnitIds(unitIds);
        setConditionDetail(cond, userId);
        PageRequest<IncomeResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<IncomeResVO> incomeList = incomeMapper.getIncomeList(cond, request);
        List<IncomeResVO> list = incomeList.getList();
        Map<String, String> typeMap = getInvoiceType();
        Map<String, String> categoryMap = getCategory();
        Map<String, String> paymentMap = getPaymentMap();
        for (IncomeResVO incomeResVO : list) {
            String category = incomeResVO.getCategory();
            // 类型
            incomeResVO.setCategory(categoryMap.get(category));
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
            // 收款类型
            incomeResVO.setPaymentType(paymentMap.get(incomeResVO.getPaymentType()));
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
        // TODO 导入存量数据时对比是否与发票或收据关联
        // 转换单位
        Map<String, Integer> unitMap = unitApiExp.getUnitMapByUnitName();
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
            income.setContractAmount(BigDecimalUtil.getBigDecimal(importVO.getContractAmount()));
            income.setInvoiceTime(DateUtil.string3DateYMD(importVO.getInvoiceTime()));
            income.setInvoiceNum(importVO.getInvoiceNum());
            income.setInvoiceType(Integer.valueOf(String.valueOf(invoiceTypeMap.get(importVO.getInvoiceType()) != null
                    ? invoiceTypeMap.get(importVO.getInvoiceType()) : 0)));
            income.setInvoiceType(Integer.valueOf(invoiceTypeMap.get(importVO.getInvoiceType())));
            income.setAmount(BigDecimalUtil.getBigDecimal(importVO.getAmount()));
            income.setTax(BigDecimalUtil.getBigDecimal(importVO.getTax()));
            income.setTaxIncludeAmount(BigDecimalUtil.getBigDecimal(importVO.getTaxIncludeAmount()));
            income.setConstructionName(importVO.getConstructionName());
            // 1 预付款   2 决算款
            income.setPaymentType(PaymentType.ADVANCE_PAYMENT.value().equals(importVO.getPaymentType()) ? 1 : 2);
            // 10-已收款 20-未收款 30-部分回款
            income.setIsReceived(IsReceived.NOT_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.NOT_RECEIVED.key()
                    : IsReceived.PART_RECEIVED.value().equals(importVO.getCategory()) ? IsReceived.PART_RECEIVED.key() : IsReceived.RECEIVED.key());
            income.setAging(importVO.getAging());
            income.setReceivable(BigDecimalUtil.getBigDecimal(importVO.getReceivable()));
            income.setReceived(BigDecimalUtil.getBigDecimal(importVO.getReceived()));
            income.setUnreceived(BigDecimalUtil.getBigDecimal(importVO.getUnreceived()));
            income.setAging(importVO.getAging());
            income.setReceivable(BigDecimalUtil.getBigDecimal(importVO.getReceivable()));
            income.setReceived(BigDecimalUtil.getBigDecimal(importVO.getReceived()));
            income.setUnreceived(BigDecimalUtil.getBigDecimal(importVO.getUnreceived()));
            //插入收款信息
            incomeMapper.insert(income);
            //插入收款下的收款详情
            if (!isEmpty(importVO.getReceiptNum())) {
                RelocationIncomeDetail incomeDetail = new RelocationIncomeDetail();
                incomeDetail.setIncomeId(income.getId());
                incomeDetail.setCreateTime(DateUtil.getCurrentDate());
                incomeDetail.setPayee(importVO.getPayee());
                incomeDetail.setReceiptNum(importVO.getReceiptNum());
                incomeDetail.setAmount(BigDecimalUtil.getBigDecimal(importVO.getMonthAmount()));
                incomeDetail.setPayMonth(DateUtil.getCurrentMonth());
                incomeDetailMapper.insert(incomeDetail);
            }
        }
    }

    @Override
    public List<IncomeExportVO> selectExportListByCondition(IncomeReqVO vo, Integer userId) {
        setConditionDetail(vo, userId);
        List<IncomeExportVO> relocationIncomeExport = incomeMapper.selectExportList(vo);
        // 类型
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        Map<String, String> categoryMap = getCategory();
        Map<Integer, String> isReceivedMap = getIsReceived();
        int i = 1;
        for (IncomeExportVO export : relocationIncomeExport) {
            String category = export.getCategory();
            export.setCategory(categoryMap.get(category));
            // 回款状态
            export.setIsReceived(isReceivedMap.get(parseInt(export.getIsReceived())));
            export.setNum(i);
            export.setUnit(unitMap.get(Integer.parseInt(export.getUnit())));
            i++;
        }
        return relocationIncomeExport;
    }


    private void setConditionDetail(IncomeReqVO cond, Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (userApi.isAdmin(userId) && cond.getUnitId() == null) {
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

    private Map<String, String> getCategory() {
        // 类型
        Map<String, String> categoryMap = new HashMap<>(100);
        categoryMap.put(Category.RELOCATION.key().toString(), Category.RELOCATION.value());
        categoryMap.put(Category.REMOVAL.key().toString(), Category.REMOVAL.value());
        categoryMap.put(Category.CONSTRUCTION.key().toString(), Category.CONSTRUCTION.value());
        return categoryMap;
    }

    private Map<Integer, String> getIsReceived() {
        // 收款状态
        Map<Integer, String> statusMap = new HashMap<>(100);
        statusMap.put(IsReceived.RECEIVED.key(), IsReceived.RECEIVED.value());
        statusMap.put(IsReceived.NOT_RECEIVED.key(), IsReceived.NOT_RECEIVED.value());
        statusMap.put(IsReceived.PART_RECEIVED.key(), IsReceived.PART_RECEIVED.value());
        return statusMap;
    }

    private Map<String, String> getPaymentMap() {
        Map<String, String> paymentMap = new HashMap<>(100);
        paymentMap.put(PaymentType.ADVANCE_PAYMENT.key().toString(), PaymentType.ADVANCE_PAYMENT.value());
        paymentMap.put(PaymentType.FINAL_PARAGRAPH.key().toString(), PaymentType.FINAL_PARAGRAPH.value());
        paymentMap.put(PaymentType.FINAL_PAYMENT.key().toString(), PaymentType.FINAL_PAYMENT.value());
        return paymentMap;
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue().equals(name))) {
            throw new RelocationException(RelocationErrorCode.FILE_DATA_NAME_ERROR);
        }
    }

}
